package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.PriceDocuments;
import com.touchmind.core.mongo.repository.generic.GenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceDocumentsRepository extends GenericRepository<PriceDocuments> {
}
