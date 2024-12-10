package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.DataSourceInput;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;


@Repository("DataSourceInputRepository")
public interface DataSourceInputRepository extends GenericImportRepository<DataSourceInput> {
    DataSourceInput findByFieldNameAndDataSourceId(String fileName, String id);

    DataSourceInput findByRecordId(String dataSourceInputsId);
}
