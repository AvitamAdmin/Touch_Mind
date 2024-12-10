package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Model;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ModelRepository")
public interface ModelRepository extends GenericImportRepository<Model> {
    Model findByIdentifier(String modelId);

    Model findByRecordId(String id);

    void deleteByRecordId(String valueOf);

    List<Model> findByStatusOrderByIdentifier(Boolean status);

    List<Model> findByStatusAndSubsidiariesOrderByIdentifier(Boolean status, String subId);


}
