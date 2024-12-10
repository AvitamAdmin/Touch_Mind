package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.CrawlerPath;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CrawlerPathRepository")
public interface CrawlerPathRepository extends MongoRepository<CrawlerPath, Long> {
    CrawlerPath findByPathCategory(String pathCategory);

    List<CrawlerPath> findByPathCategoryAndSites(String pathCategory, String site);

    CrawlerPath findByRecordId(String recordId);

    void deleteByRecordId(Long id);

    List<CrawlerPath> findByStatusOrderByIdentifier(boolean b);
}
