package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Action;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ActionRepository")
public interface ActionRepository extends GenericImportRepository<Action> {
    List<Action> findByStatusOrderByIdentifier(Boolean status);

    Action findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

}
