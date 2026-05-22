package com.kinedical.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kinedical.model.HealthContent;
import com.kinedical.service.HealthContentService;

@RestController
@RequestMapping("/api/health-contents")
public class HealthContentController {

    private final HealthContentService healthContentService;

    public HealthContentController(HealthContentService healthContentService) {
        this.healthContentService = healthContentService;
    }

    @GetMapping
    public List<HealthContent> listPublished() {
        return healthContentService.findPublished();
    }

    @GetMapping("/published")
    public List<HealthContent> getPublished() {
        return healthContentService.findPublished();
    }

    @GetMapping("/category/{category}")
    public List<HealthContent> getByCategory(@PathVariable HealthContent.ContentCategory category) {
        return healthContentService.findByCategory(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthContent> getById(@PathVariable String id) {
        return healthContentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public HealthContent create(@RequestBody HealthContent content) {
        if (content.getCreatedAt() == null) {
            content.setCreatedAt(java.time.Instant.now());
        }
        content.setUpdatedAt(java.time.Instant.now());
        return healthContentService.create(content);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthContent> update(@PathVariable String id, @RequestBody HealthContent content) {
        try {
            content.setUpdatedAt(java.time.Instant.now());
            HealthContent updated = healthContentService.update(id, content);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        healthContentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
