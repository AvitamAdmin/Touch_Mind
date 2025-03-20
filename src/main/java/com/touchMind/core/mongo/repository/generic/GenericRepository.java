package com.touchMind.core.mongo.repository.generic;

import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenericRepository<T extends BaseEntity> extends MongoRepository<T, Long> {
    BaseEntity findByIdentifierAndRelationId(String recordId, String relationId);

    List<BaseEntity> findByIdentifier(String identifier);

    List<BaseEntity> findByIdentifierAndRelationIdAndSessionId(String recordId, String relationId, String sessionId);

    List<BaseEntity> findByIndexIdAndIdentifierAndRelationIdAndSessionId(Integer indexId, String recordId, String relationId, String sessionId);

    List<BaseEntity> findByIdentifierAndSessionId(String recordId, String sessionId);

    List<BaseEntity> findByIndexIdAndIdentifierAndSessionId(Integer indexId, String recordId, String sessionId);

    List<BaseEntity> getBySessionId(String sessionId);

    Page<BaseEntity> getBySessionId(String sessionId, Pageable pageable);

    void deleteAllBySessionId(String sessionId);
}
