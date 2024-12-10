package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.DataSourceInput;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;


@Repository("DataSourceInputRepository")
public interface DataSourceInputRepository extends GenericImportRepository<DataSourceInput> {
    DataSourceInput findByFieldNameAndDataSourceId(String fileName, String id);

    DataSourceInput findByRecordId(String dataSourceInputsId);
}
