package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Dashboard;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DashboardRepository")
public interface DashboardRepository extends GenericImportRepository<Dashboard> {
    Dashboard findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Dashboard findByNode(String node);

    List<Dashboard> findByStatusOrderByIdentifier(Boolean b);


}
