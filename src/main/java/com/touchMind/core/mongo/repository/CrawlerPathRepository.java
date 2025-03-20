package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.CrawlerPath;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CrawlerPathRepository")
public interface CrawlerPathRepository extends GenericImportRepository<CrawlerPath> {
    CrawlerPath findByPathCategory(String pathCategory);

    List<CrawlerPath> findByPathCategoryAndSites(String pathCategory, String site);

    CrawlerPath findByIdentifier(String identifier);

    void deleteByIdentifier(String id);

    List<CrawlerPath> findByStatusOrderByIdentifier(boolean b);
}
