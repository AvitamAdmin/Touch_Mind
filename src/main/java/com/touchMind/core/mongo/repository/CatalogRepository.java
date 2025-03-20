package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Catalog;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CatalogRepository")
public interface CatalogRepository extends GenericImportRepository<Catalog> {

    Catalog findByIdentifier(String id);

    List<Catalog> findByStatusOrderByIdentifier(boolean status);

    void deleteByIdentifier(String valueOf);
}
