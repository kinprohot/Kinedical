package com.kinedical.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kinedical.dto.RecommendRequest;
import com.kinedical.dto.RecommendResponse;
import com.kinedical.model.HealthContent;
import com.kinedical.model.Interaction;
import com.kinedical.model.User;
import com.kinedical.repository.HealthContentRepository;
import com.kinedical.repository.InteractionRepository;
import com.kinedical.repository.UserRepository;
import com.kinedical.security.AppUserDetails;
import com.kinedical.service.RecommendClientService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;
    private final HealthContentRepository healthContentRepository;
    private final RecommendClientService recommendClientService;

    public RecommendationController(UserRepository userRepository,
                                    InteractionRepository interactionRepository,
                                    HealthContentRepository healthContentRepository,
                                    RecommendClientService recommendClientService) {
        this.userRepository = userRepository;
        this.interactionRepository = interactionRepository;
        this.healthContentRepository = healthContentRepository;
        this.recommendClientService = recommendClientService;
    }

    @GetMapping
    public ResponseEntity<List<HealthContent>> getRecommendations(
            @RequestParam(value = "userId", required = false) String userId,
            Authentication authentication) {

        String activeUserId = resolveUserId(authentication);
        if (activeUserId == null || activeUserId.trim().isEmpty()) {
            activeUserId = userId;
        }

        List<HealthContent> published = healthContentRepository.findByStatusOrderByPublishDateDesc(HealthContent.ContentStatus.PUBLISHED);
        if (published.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        RecommendRequest request = new RecommendRequest();
        request.setUserId(activeUserId != null ? activeUserId : "anonymous");

        // Map health contents to RecommendRequest.Item
        List<RecommendRequest.Item> items = published.stream().map(c -> {
            RecommendRequest.Item item = new RecommendRequest.Item();
            item.setItemId(c.getId());
            item.setContent(c.getTitle() + " " + (c.getSummary() != null ? c.getSummary() : "") + " " + (c.getBody() != null ? c.getBody() : ""));
            item.setViews(c.getStats() != null && c.getStats().getViews() != null ? c.getStats().getViews() : 0);
            item.setLikes(c.getStats() != null && c.getStats().getLikes() != null ? c.getStats().getLikes() : 0);
            item.setSaves(c.getStats() != null && c.getStats().getCommentsCount() != null ? c.getStats().getCommentsCount() : 0);
            item.setPublishedAt(c.getPublishDate() != null ? c.getPublishDate().toString() : Instant.now().toString());
            item.setVector(c.getVector());
            return item;
        }).toList();
        request.setItems(items);

        // If user is logged in, fetch user details & interactions to pass to FastAPI
        if (activeUserId != null && !activeUserId.trim().isEmpty() && !"anonymous".equalsIgnoreCase(activeUserId)) {
            Optional<User> userOpt = userRepository.findById(activeUserId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                request.setUserVector(user.getVector());

                // Get interaction history to build history vectors
                List<Interaction> interactions = interactionRepository.findByPatientId(activeUserId);
                if (interactions != null && !interactions.isEmpty()) {
                    List<List<Double>> historyVectors = interactions.stream()
                            .map(i -> healthContentRepository.findById(i.getContentId()))
                            .filter(Optional::isPresent)
                            .map(opt -> opt.get().getVector())
                            .filter(v -> v != null && !v.isEmpty())
                            .toList();
                    request.setHistoryVectors(historyVectors);
                }
            }
        }

        try {
            RecommendResponse response = recommendClientService.recommendAsync(request).block();
            List<HealthContent> sortedContents = new java.util.ArrayList<>();
            if (response != null && response.getRecommendations() != null) {
                for (RecommendResponse.RecommendItem rec : response.getRecommendations()) {
                    healthContentRepository.findById(rec.getItemId()).ifPresent(sortedContents::add);
                }
            }
            // Fallback to published if for some reason FastAPI returns nothing
            if (sortedContents.isEmpty()) {
                sortedContents.addAll(published.stream().limit(5).toList());
            }
            return ResponseEntity.ok(sortedContents);
        } catch (Exception ex) {
            // Fallback on error to default published list
            List<HealthContent> fallback = published.stream().limit(5).toList();
            return ResponseEntity.ok(fallback);
        }
    }

    private String resolveUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails details) {
            return details.getUserId();
        }
        return null;
    }
}
