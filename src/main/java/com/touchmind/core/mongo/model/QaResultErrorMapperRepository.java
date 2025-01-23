package com.touchmind.core.mongo.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QaResultErrorMapperRepository extends MongoRepository<QaResultErrorMapper, String> {
    QaResultErrorMapper findByRecordId(Long recordId);

    void deleteByRecordId(Long recordId);

    List<QaResultErrorMapper> findAllByOrderByIdentifier();
}
