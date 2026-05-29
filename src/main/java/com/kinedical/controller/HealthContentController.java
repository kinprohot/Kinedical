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
        System.out.println("[DEBUG] listPublished (root) called");
        List<HealthContent> res = healthContentService.findPublished();
        System.out.println("[DEBUG] listPublished returned count: " + (res != null ? res.size() : "null"));
        return res;
    }

    @GetMapping("/published")
    public List<HealthContent> getPublished() {
        System.out.println("[DEBUG] getPublished (/published) called");
        List<HealthContent> res = healthContentService.findPublished();
        System.out.println("[DEBUG] getPublished returned count: " + (res != null ? res.size() : "null"));
        return res;
    }

    @GetMapping("/category/{category}")
    public List<HealthContent> getByCategory(@PathVariable("category") String category) {
        System.out.println("[DEBUG] getByCategory called with: " + category);
        try {
            if (category == null || category.trim().isEmpty()) {
                System.out.println("[DEBUG] Category is null or empty");
                return List.of();
            }
            String upper = category.toUpperCase().trim();
            System.out.println("[DEBUG] Upper case category: " + upper);
            HealthContent.ContentCategory cat = HealthContent.ContentCategory.valueOf(upper);
            System.out.println("[DEBUG] Mapped to enum: " + cat);
            List<HealthContent> res = healthContentService.findByCategory(cat);
            System.out.println("[DEBUG] findByCategory returned count: " + (res != null ? res.size() : "null"));
            return res;
        } catch (Exception ex) {
            System.out.println("[DEBUG] Exception in getByCategory: " + ex.getMessage());
            ex.printStackTrace();
            return List.of();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthContent> getById(@PathVariable("id") String id) {
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
    public ResponseEntity<HealthContent> update(@PathVariable("id") String id, @RequestBody HealthContent content) {
        try {
            content.setUpdatedAt(java.time.Instant.now());
            HealthContent updated = healthContentService.update(id, content);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        healthContentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
