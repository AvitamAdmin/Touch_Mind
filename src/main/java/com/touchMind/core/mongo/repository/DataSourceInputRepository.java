package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.DataSourceInput;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;


@Repository("DataSourceInputRepository")
public interface DataSourceInputRepository extends GenericImportRepository<DataSourceInput> {
    DataSourceInput findByFieldNameAndDataSourceId(String fileName, String id);

    DataSourceInput findByIdentifier(String dataSourceInputsId);
}
