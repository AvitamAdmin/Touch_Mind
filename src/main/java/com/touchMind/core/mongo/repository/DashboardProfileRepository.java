package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.DashboardProfile;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("DashboardProfileRepository")
public interface DashboardProfileRepository extends GenericImportRepository<DashboardProfile> {
    DashboardProfile findByIdentifier(String id);

    List<DashboardProfile> findByStatusOrderByIdentifier(boolean status);


}
