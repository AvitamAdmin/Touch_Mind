package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Model;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ModelRepository")
public interface ModelRepository extends GenericImportRepository<Model> {
    Model findByIdentifier(String modelId);

    void deleteByIdentifier(String valueOf);

    List<Model> findByStatusOrderByIdentifier(Boolean status);

    List<Model> findByStatusAndSubsidiariesOrderByIdentifier(Boolean status, String subId);


}
