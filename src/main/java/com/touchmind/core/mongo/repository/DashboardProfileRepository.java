package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.DashboardProfile;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("DashboardProfileRepository")
public interface DashboardProfileRepository extends GenericImportRepository<DashboardProfile> {
    DashboardProfile findByRecordId(String id);

    List<DashboardProfile> findByStatusOrderByIdentifier(boolean status);


}
