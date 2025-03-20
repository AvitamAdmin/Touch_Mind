package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.MessageResource;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MessageResourceRepository")
public interface MessageResourceRepository extends GenericImportRepository<MessageResource> {
    MessageResource findByIdentifier(String id);

    MessageResource deleteByIdentifier(String id);

    List<MessageResource> findByTestPlanId(String groupId);

    List<MessageResource> findByStatusOrderByIdentifier(Boolean status);
}
