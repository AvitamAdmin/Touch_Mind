package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.ProductPrices;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProductPricesRepository")
public interface ProductPricesRepository extends GenericImportRepository<ProductPrices> {
    List<ProductPrices> findByStockId(Long stockId);
}
