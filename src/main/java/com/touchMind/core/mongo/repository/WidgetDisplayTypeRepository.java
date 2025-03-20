package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.WidgetDisplayType;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("WidgetDisplayTypeRepository")
public interface WidgetDisplayTypeRepository extends GenericImportRepository<WidgetDisplayType> {
    WidgetDisplayType findByIdentifier(String subId);

    List<WidgetDisplayType> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String valueOf);

}
