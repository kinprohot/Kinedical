package com.kinedical.controller;

import java.time.Instant;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kinedical.model.Interaction;
import com.kinedical.repository.InteractionRepository;
import com.kinedical.security.AppUserDetails;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {

    private final InteractionRepository interactionRepository;

    public InteractionController(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    @PostMapping
    public ResponseEntity<Interaction> recordInteraction(@RequestBody InteractionRequest request, Authentication authentication) {
        String patientId = resolveUserId(authentication);
        if (patientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (request.getContentId() == null || request.getActionType() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Calculate weight based on type
        double weight = 1.0;
        switch (request.getActionType()) {
            case LIKE:
                weight = 2.0;
                break;
            case SAVE:
                weight = 3.0;
                break;
            case VIEW:
            default:
                weight = 1.0;
                break;
        }

        // Check if an interaction of this type already exists to avoid duplicate bloating, or just record all
        // To be safe and keep history clean, let's look if there is an exact same action, or just log
        Interaction interaction = new Interaction();
        interaction.setPatientId(patientId);
        interaction.setContentId(request.getContentId());
        interaction.setActionType(request.getActionType());
        interaction.setWeight(weight);
        interaction.setCreatedAt(Instant.now());

        Interaction saved = interactionRepository.save(interaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    private String resolveUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails details) {
            return details.getUserId();
        }
        return null;
    }

    public static class InteractionRequest {
        private String contentId;
        private Interaction.ActionType actionType;

        public String getContentId() {
            return contentId;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
        }

        public Interaction.ActionType getActionType() {
            return actionType;
        }

        public void setActionType(Interaction.ActionType actionType) {
            this.actionType = actionType;
        }
    }
}
