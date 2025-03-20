package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Action;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ActionRepository")
public interface ActionRepository extends GenericImportRepository<Action> {
    List<Action> findByStatusOrderByIdentifier(Boolean status);

    Action findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

}
