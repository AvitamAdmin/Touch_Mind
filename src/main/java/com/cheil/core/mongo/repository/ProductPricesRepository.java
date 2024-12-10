package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.ProductPrices;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProductPricesRepository")
public interface ProductPricesRepository extends GenericImportRepository<ProductPrices> {
    List<ProductPrices> findByStockId(Long stockId);
}
