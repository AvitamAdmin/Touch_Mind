package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.WebSite;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebSiteRepository extends GenericImportRepository<WebSite> {
    WebSite findByIdentifier(String identifier);

    WebSite deleteByIdentifier(String identifier);

    List<WebSite> findAllByOrderByIdentifier();
}
