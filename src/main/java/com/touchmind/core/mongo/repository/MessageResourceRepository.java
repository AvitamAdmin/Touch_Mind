package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.MessageResource;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MessageResourceRepository")
public interface MessageResourceRepository extends GenericImportRepository<MessageResource> {
    MessageResource findByRecordId(String id);

    MessageResource deleteByRecordId(String id);

    List<MessageResource> findByTestPlanId(String groupId);

    List<MessageResource> findByStatusOrderByIdentifier(Boolean status);
}
