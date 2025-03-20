package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.WidgetManagement;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("WidgetManagementRepository")
public interface WidgetManagementRepository extends GenericImportRepository<WidgetManagement> {
    WidgetManagement findByIdentifier(String subId);

    List<WidgetManagement> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String valueOf);

}
