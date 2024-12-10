package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.StockDocuments;
import com.cheil.core.mongo.repository.generic.GenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDocumentsRepository extends GenericRepository<StockDocuments> {
}
