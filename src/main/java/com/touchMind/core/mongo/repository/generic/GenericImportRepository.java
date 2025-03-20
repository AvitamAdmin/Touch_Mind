package com.touchMind.core.mongo.repository.generic;

import com.touchMind.core.mongo.model.CommonFields;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenericImportRepository<T extends CommonFields> extends MongoRepository<T, ObjectId> {
    CommonFields findByIdentifier(String identifier);

    CommonFields findByIdentifierIgnoreCase(String identifier);
}
