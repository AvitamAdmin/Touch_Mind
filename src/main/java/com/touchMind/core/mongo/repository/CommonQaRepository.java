package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.CommonQaFields;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CommonQaRepository<T extends CommonQaFields> extends MongoRepository<T, ObjectId> {
    T findByIdentifier(String identifier);
}
