package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.dto.LocatorsGroupPriorityDto;
import com.touchMind.core.mongo.model.LocatorsGroupPriority;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;

import java.util.List;

public interface LocatorsGroupPriorityRepository extends GenericImportRepository<LocatorsGroupPriority> {
    List<LocatorsGroupPriorityDto> findByStatusOrderByIdentifier(boolean b);

    List<LocatorsGroupPriorityDto> findByStatus(boolean b);

    void deleteByIdentifier(String identifier);

    LocatorsGroupPriority findByIdentifier(String identifier);
}
