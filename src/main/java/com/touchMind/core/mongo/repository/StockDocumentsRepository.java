package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.StockDocuments;
import com.touchMind.core.mongo.repository.generic.GenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDocumentsRepository extends GenericRepository<StockDocuments> {
}
