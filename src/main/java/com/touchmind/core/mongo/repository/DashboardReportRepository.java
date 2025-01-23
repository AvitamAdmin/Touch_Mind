package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.DashboardReport;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DashboardReportRepository")
public interface DashboardReportRepository extends GenericImportRepository<DashboardReport> {
    DashboardReport findByIdentifier(String subId);

    List<DashboardReport> findByStatusOrderByIdentifier(Boolean status);

    DashboardReport findByRecordId(String recordId);

    void deleteByRecordId(String valueOf);

}
