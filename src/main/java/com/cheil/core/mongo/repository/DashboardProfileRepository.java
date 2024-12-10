package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.DashboardProfile;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("DashboardProfileRepository")
public interface DashboardProfileRepository extends GenericImportRepository<DashboardProfile> {
    DashboardProfile findByRecordId(String id);

    List<DashboardProfile> findByStatusOrderByIdentifier(boolean status);


}
