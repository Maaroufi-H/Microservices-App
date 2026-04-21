package net.maaroufi.claudeassistant.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.maaroufi.claudeassistant.dto.ChatRequest;
import net.maaroufi.claudeassistant.dto.ChatResponse;
import net.maaroufi.claudeassistant.dto.ConversationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ClaudeService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeService.class);
    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-opus-4-7";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private static final String DEFAULT_SYSTEM_PROMPT =
            "You are an expert software architect and developer assistant integrated into a Spring Boot microservices application. " +
            "The project is called Microservices-App and consists of: discovery-service (Eureka, port 8761), " +
            "gateway-service (Spring Cloud Gateway, port 8088), customer-service (port 8082), " +
            "product-service (port 8081), Inventory-service (port 8083), order-service (port 8084), " +
            "payment-service (port 8085), tracking-service (port 8085, behavior analytics), " +
            "recommendation-service (port 8086, Azure ML), monitoring-service (port 8087), " +
            "geolocation-service (port 8090), and claude-assistant-service (port 8091, this service). " +
            "All services use Java 17, Spring Boot 3.5.3, Spring Cloud 2025.0.0, and PostgreSQL (Railway). " +
            "Kafka has been removed in favor of direct REST/Feign communication. " +
            "Help the developer understand, modify, and extend the application. " +
            "Provide concrete code examples with correct package names (net.maaroufi.*). " +
            "Be concise and precise.";

    @Value("${anthropic.api-key:}")
    private String apiKey;

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final ExecutorService streamExecutor = Executors.newCachedThreadPool();

    public ClaudeService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(ANTHROPIC_API_URL)
                .defaultHeader("anthropic-version", ANTHROPIC_VERSION)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public ChatResponse chat(ChatRequest request) {
        String key = resolveApiKey();
        if (key == null || key.isBlank()) {
            return new ChatResponse("Claude API key not configured. Set the ANTHROPIC_API_KEY environment variable.", MODEL, 0, 0);
        }

        try {
            ObjectNode body = buildRequestBody(request, false);
            String responseJson = restClient.post()
                    .header("x-api-key", key)
                    .body(body.toString())
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseJson);
            String text = extractText(root);
            long inputTokens = root.path("usage").path("input_tokens").asLong(0);
            long outputTokens = root.path("usage").path("output_tokens").asLong(0);
            String model = root.path("model").asText(MODEL);

            return new ChatResponse(text, model, inputTokens, outputTokens);
        } catch (Exception e) {
            log.error("Claude API error", e);
            return new ChatResponse("Error calling Claude API: " + e.getMessage(), MODEL, 0, 0);
        }
    }

    public SseEmitter streamChat(ChatRequest request) {
        SseEmitter emitter = new SseEmitter(120_000L);
        String key = resolveApiKey();

        if (key == null || key.isBlank()) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Claude API key not configured. Set ANTHROPIC_API_KEY env var."));
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        streamExecutor.submit(() -> {
            try {
                ObjectNode body = buildRequestBody(request, true);

                restClient.post()
                        .header("x-api-key", key)
                        .body(body.toString())
                        .exchange((req, response) -> {
                            try (InputStream is = response.getBody();
                                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.startsWith("data:")) {
                                        String data = line.substring(5).trim();
                                        if ("[DONE]".equals(data)) break;

                                        try {
                                            JsonNode event = objectMapper.readTree(data);
                                            String type = event.path("type").asText("");
                                            if ("content_block_delta".equals(type)) {
                                                String delta = event.path("delta").path("text").asText("");
                                                if (!delta.isEmpty()) {
                                                    emitter.send(SseEmitter.event().name("delta").data(delta));
                                                }
                                            }
                                        } catch (Exception ignored) {}
                                    }
                                }
                            }
                            emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                            emitter.complete();
                            return null;
                        });
            } catch (Exception e) {
                log.error("Streaming error", e);
                try {
                    emitter.send(SseEmitter.event().name("error").data("Streaming error: " + e.getMessage()));
                } catch (IOException ignored) {}
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private ObjectNode buildRequestBody(ChatRequest request, boolean stream) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", MODEL);
        body.put("max_tokens", 8192);
        body.put("stream", stream);
        body.put("system", resolveSystemPrompt(request));

        // Adaptive thinking
        ObjectNode thinking = objectMapper.createObjectNode();
        thinking.put("type", "adaptive");
        body.set("thinking", thinking);

        ArrayNode messages = objectMapper.createArrayNode();
        addHistory(messages, request.getHistory());

        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", request.getMessage());
        messages.add(userMsg);

        body.set("messages", messages);
        return body;
    }

    private void addHistory(ArrayNode messages, List<ConversationMessage> history) {
        if (history == null || history.isEmpty()) return;
        for (ConversationMessage msg : history) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("role", msg.getRole());
            node.put("content", msg.getContent());
            messages.add(node);
        }
    }

    private String extractText(JsonNode root) {
        StringBuilder sb = new StringBuilder();
        JsonNode content = root.path("content");
        if (content.isArray()) {
            for (JsonNode block : content) {
                if ("text".equals(block.path("type").asText())) {
                    sb.append(block.path("text").asText());
                }
            }
        }
        return sb.toString();
    }

    private String resolveSystemPrompt(ChatRequest request) {
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
            return request.getSystemPrompt();
        }
        return DEFAULT_SYSTEM_PROMPT;
    }

    private String resolveApiKey() {
        if (apiKey != null && !apiKey.isBlank()) return apiKey;
        return System.getenv("ANTHROPIC_API_KEY");
    }
}
