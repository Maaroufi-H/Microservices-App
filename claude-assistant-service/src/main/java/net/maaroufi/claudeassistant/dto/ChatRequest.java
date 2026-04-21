package net.maaroufi.claudeassistant.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {
    private String message;
    private List<ConversationMessage> history;
    private String systemPrompt;
}
