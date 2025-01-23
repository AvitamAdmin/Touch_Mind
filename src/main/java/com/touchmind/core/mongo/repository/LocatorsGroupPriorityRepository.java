package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.dto.LocatorsGroupPriorityDto;
import com.touchmind.core.mongo.model.LocatorsGroupPriority;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;

import java.util.List;

public interface LocatorsGroupPriorityRepository extends GenericImportRepository<LocatorsGroupPriority> {
    List<LocatorsGroupPriorityDto> findByStatusOrderByIdentifier(boolean b);

    List<LocatorsGroupPriorityDto> findByStatus(boolean b);

    void deleteByRecordId(String recordId);

    LocatorsGroupPriority findByRecordId(String recordId);
}
