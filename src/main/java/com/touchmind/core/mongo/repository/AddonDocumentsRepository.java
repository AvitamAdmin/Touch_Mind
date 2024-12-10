package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.AddonDocuments;
import com.touchmind.core.mongo.repository.generic.GenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddonDocumentsRepository extends GenericRepository<AddonDocuments> {
}