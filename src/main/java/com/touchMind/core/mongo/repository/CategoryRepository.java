package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Category;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository("CategoryRepository")
public interface CategoryRepository extends GenericImportRepository<Category> {
    Category findByIdentifier(String id);

    List<Category> findByStatus(boolean b);

    void deleteByIdentifier(String valueOf);

    List<Category> findByStatusOrderByIdentifier(Boolean status);

    List<Category> findByStatusAndSubsidiariesOrderByIdentifier(Boolean status, String subId);

    List<Category> findByStatusAndSubsidiariesOrderByIdentifier(Boolean status, Set<String> subIds);

}
