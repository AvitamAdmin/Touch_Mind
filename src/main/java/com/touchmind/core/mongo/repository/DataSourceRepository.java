package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.DataSource;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DataSourceRepository")
public interface DataSourceRepository extends GenericImportRepository<DataSource> {

    DataSource findByRecordId(String id);

    void deleteByRecordId(String valueOf);

    List<DataSource> findByStatusOrderByIdentifier(Boolean status);

}
