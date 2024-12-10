package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Variant;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("VariantRepository")
public interface VariantRepository extends GenericImportRepository<Variant> {
    List<Variant> findAllByIdentifier(Long id);

    Variant findByIdentifier(String id);

    @Query("FROM Variant WHERE category = ?1")
    List<Variant> findAllByCategoryId(String category);

    @Query("FROM Variant WHERE model = ?1")
    List<Variant> findAllByIdentifier(String modelId);

    List<Variant> findByModel_identifier(String modelId);

    List<Variant> findByCategory_identifier(String identifier);

    Variant findByRecordId(String id);

    void deleteByRecordId(String valueOf);

    List<Variant> findByStatusOrderByIdentifier(Boolean status);
}
