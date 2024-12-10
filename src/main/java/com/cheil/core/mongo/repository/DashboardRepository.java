package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Dashboard;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DashboardRepository")
public interface DashboardRepository extends GenericImportRepository<Dashboard> {
    Dashboard findByIdentifier(String identifier);

    Dashboard findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

    Dashboard findByNode(String node);

    List<Dashboard> findByStatusOrderByIdentifier(Boolean b);


}
