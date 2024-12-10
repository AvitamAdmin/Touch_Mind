package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Category;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CategoryRepository")
public interface CategoryRepository extends GenericImportRepository<Category> {
    Category findByIdentifier(String id);

    List<Category> findByStatus(boolean b);

    Category findByRecordId(String recordId);

    void deleteByRecordId(String valueOf);

    List<Category> findByStatusOrderByIdentifier(Boolean status);

    List<Category> findByStatusAndSubsidiariesOrderByIdentifier(Boolean status, String subId);

}
