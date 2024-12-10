package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Stock;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("StockRepository")
public interface StockRepository extends GenericImportRepository<Stock> {
    List<Stock> findByProductCode(String productCode);

    Page<Stock> findByProductCode(String productCode, Pageable pageable);

    @Transactional
    long deleteBySessionId(String currentSession);
}
