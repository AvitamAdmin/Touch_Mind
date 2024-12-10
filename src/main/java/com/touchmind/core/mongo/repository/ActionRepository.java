package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Action;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ActionRepository")
public interface ActionRepository extends GenericImportRepository<Action> {
    List<Action> findByStatusOrderByIdentifier(Boolean status);

    Action findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

}
