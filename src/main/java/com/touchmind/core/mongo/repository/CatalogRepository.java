package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Catalog;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CatalogRepository")
public interface CatalogRepository extends GenericImportRepository<Catalog> {

    Catalog findByRecordId(String id);

    List<Catalog> findByStatusOrderByIdentifier(boolean status);

    void deleteByRecordId(String valueOf);
}
