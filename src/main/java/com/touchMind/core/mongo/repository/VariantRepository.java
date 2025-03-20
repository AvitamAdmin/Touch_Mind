package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Variant;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("VariantRepository")
public interface VariantRepository extends GenericImportRepository<Variant> {
    List<Variant> findAllByIdentifier(String id);

    Variant findByIdentifier(String id);

    @Query("FROM Variant WHERE category = ?1")
    List<Variant> findAllByCategoryId(String category);

    List<Variant> findByModel_identifier(String modelId);

    List<Variant> findByCategory_identifier(String identifier);

    void deleteByIdentifier(String valueOf);

    List<Variant> findByStatusOrderByIdentifier(Boolean status);
}
