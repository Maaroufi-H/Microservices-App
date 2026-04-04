package net.maaroufi.recommendationservice.controllers;

import net.maaroufi.recommendationservice.dto.RecommendationResponse;
import net.maaroufi.recommendationservice.services.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Returns a ranked list of product recommendations for a given customer.
     *
     * When Azure ML endpoint is configured: returns model predictions.
     * When not yet configured: returns popular products as fallback.
     *
     * GET /api/recommendations/{customerId}
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(@PathVariable Long customerId) {
        RecommendationResponse response = recommendationService.getRecommendations(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Anonymous recommendation endpoint using session ID.
     * Useful before the user is identified (no customerId yet).
     *
     * GET /api/recommendations/anonymous/{sessionId}
     * Currently returns popular products; will be powered by session-based
     * ML features once enough data is collected.
     */
    @GetMapping("/anonymous/{sessionId}")
    public ResponseEntity<RecommendationResponse> getAnonymousRecommendations(
            @PathVariable String sessionId) {
        // Use -1L as a sentinel for anonymous sessions
        RecommendationResponse response = recommendationService.getRecommendations(-1L);
        response.setCustomerId(null);
        return ResponseEntity.ok(response);
    }
}
