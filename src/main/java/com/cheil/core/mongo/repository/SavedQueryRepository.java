package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.SavedQuery;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SavedQueryRepository")
public interface SavedQueryRepository extends MongoRepository<SavedQuery, ObjectId> {

    SavedQuery findByRecordId(String recordId);

    SavedQuery findByIdentifier(String identifier);

    void deleteByRecordId(String valueOf);

    List<SavedQuery> findByUserAndSourceItemOrderByIdentifier(String user, String sourceItem);
}
