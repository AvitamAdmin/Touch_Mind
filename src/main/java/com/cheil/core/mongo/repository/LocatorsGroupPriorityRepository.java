package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.dto.LocatorsGroupPriorityDto;
import com.cheil.core.mongo.model.LocatorsGroupPriority;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;

import java.util.List;

public interface LocatorsGroupPriorityRepository extends GenericImportRepository<LocatorsGroupPriority> {
    List<LocatorsGroupPriorityDto> findByStatusOrderByIdentifier(boolean b);

    List<LocatorsGroupPriorityDto> findByStatus(boolean b);

    void deleteByRecordId(String recordId);

    LocatorsGroupPriority findByRecordId(String recordId);
}
