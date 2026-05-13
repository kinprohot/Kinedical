package com.kinedical.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kinedical.model.HealthContent;

public interface HealthContentRepository extends MongoRepository<HealthContent, String> {
    List<HealthContent> findByStatusOrderByPublishDateDesc(HealthContent.ContentStatus status);

    List<HealthContent> findByCategoryAndStatusOrderByPublishDateDesc(HealthContent.ContentCategory category,
            HealthContent.ContentStatus status);
}
