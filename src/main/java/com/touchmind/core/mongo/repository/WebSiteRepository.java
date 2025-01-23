package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.WebSite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebSiteRepository extends MongoRepository<WebSite, String> {
    WebSite findByRecordId(String recordId);

    WebSite deleteByRecordId(String recordId);

    List<WebSite> findAllByOrderByIdentifier();
}
