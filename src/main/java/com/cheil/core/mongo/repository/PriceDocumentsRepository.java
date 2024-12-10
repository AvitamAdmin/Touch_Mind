package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.PriceDocuments;
import com.cheil.core.mongo.repository.generic.GenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceDocumentsRepository extends GenericRepository<PriceDocuments> {
}
