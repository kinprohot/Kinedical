package com.kinedical.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kinedical.model.HealthContent;
import com.kinedical.repository.HealthContentRepository;

@Service
public class HealthContentService {

    private final HealthContentRepository healthContentRepository;

    public HealthContentService(HealthContentRepository healthContentRepository) {
        this.healthContentRepository = healthContentRepository;
    }

    public List<HealthContent> findAll() {
        return healthContentRepository.findAll();
    }

    public Optional<HealthContent> findById(String id) {
        return healthContentRepository.findById(id);
    }

    public List<HealthContent> findPublished() {
        return healthContentRepository.findByStatusOrderByPublishDateDesc(HealthContent.ContentStatus.PUBLISHED);
    }

    public List<HealthContent> findByCategory(HealthContent.ContentCategory category) {
        return healthContentRepository.findByCategoryAndStatusOrderByPublishDateDesc(
                category, HealthContent.ContentStatus.PUBLISHED);
    }

    public HealthContent create(HealthContent content) {
        return healthContentRepository.save(content);
    }

    public HealthContent update(String id, HealthContent updatedContent) {
        return healthContentRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updatedContent.getTitle());
                    existing.setSlug(updatedContent.getSlug());
                    existing.setSummary(updatedContent.getSummary());
                    existing.setBody(updatedContent.getBody());
                    existing.setAuthorId(updatedContent.getAuthorId());
                    existing.setAuthorName(updatedContent.getAuthorName());
                    existing.setCategory(updatedContent.getCategory());
                    existing.setTags(updatedContent.getTags());
                    existing.setStatus(updatedContent.getStatus());
                    existing.setPublishDate(updatedContent.getPublishDate());
                    existing.setLanguage(updatedContent.getLanguage());
                    existing.setFeaturedImage(updatedContent.getFeaturedImage());
                    existing.setReadTimeMinutes(updatedContent.getReadTimeMinutes());
                    existing.setMeta(updatedContent.getMeta());
                    existing.setStats(updatedContent.getStats());
                    existing.setRelatedContentIds(updatedContent.getRelatedContentIds());
                    existing.setVector(updatedContent.getVector());
                    existing.setUpdatedAt(updatedContent.getUpdatedAt());
                    return healthContentRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("HealthContent not found: " + id));
    }

    public void delete(String id) {
        healthContentRepository.deleteById(id);
    }
}
