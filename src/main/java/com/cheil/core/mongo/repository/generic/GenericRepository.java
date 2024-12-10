package com.cheil.core.mongo.repository.generic;

import com.cheil.core.mongo.model.baseEntity.BaseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenericRepository<T extends BaseEntity> extends MongoRepository<T, Long> {
    BaseEntity findByRecordIdAndRelationId(String recordId, String relationId);

    List<BaseEntity> findByRecordId(String recordId);

    List<BaseEntity> findByRecordIdAndRelationIdAndSessionId(String recordId, String relationId, String sessionId);

    List<BaseEntity> findByIndexIdAndRecordIdAndRelationIdAndSessionId(Integer indexId, String recordId, String relationId, String sessionId);

    List<BaseEntity> findByRecordIdAndSessionId(String recordId, String sessionId);

    List<BaseEntity> findByIndexIdAndRecordIdAndSessionId(Integer indexId, String recordId, String sessionId);

    List<BaseEntity> getBySessionId(String sessionId);

    List<BaseEntity> getBySessionId(String sessionId, Pageable pageable);

    void deleteAllBySessionId(String sessionId);
}
