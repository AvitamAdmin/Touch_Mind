package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.DataSource;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DataSourceRepository")
public interface DataSourceRepository extends GenericImportRepository<DataSource> {

    DataSource findByIdentifier(String id);

    void deleteByIdentifier(String valueOf);

    List<DataSource> findByStatusOrderByIdentifier(Boolean status);

}
