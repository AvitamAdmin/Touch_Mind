package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.DashboardReport;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DashboardReportRepository")
public interface DashboardReportRepository extends GenericImportRepository<DashboardReport> {
    DashboardReport findByIdentifier(String subId);

    List<DashboardReport> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String valueOf);

}
