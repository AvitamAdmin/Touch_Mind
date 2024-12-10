package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.StockDocuments;
import com.touchmind.core.mongo.repository.generic.GenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDocumentsRepository extends GenericRepository<StockDocuments> {
}
