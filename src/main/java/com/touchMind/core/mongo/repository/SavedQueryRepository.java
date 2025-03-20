package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.SavedQuery;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SavedQueryRepository")
public interface SavedQueryRepository extends GenericImportRepository<SavedQuery> {

    SavedQuery findByIdentifier(String identifier);

    void deleteByIdentifier(String valueOf);

    List<SavedQuery> findByUserAndSourceItemOrderByIdentifier(String user, String sourceItem);
}
