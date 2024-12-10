package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.model.DataSource;
import com.touchmind.core.mongo.repository.DataSourceRepository;
import com.touchmind.core.service.FileHandlerService;
import com.touchmind.core.service.ServiceUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileHandlerServiceImpl implements FileHandlerService {

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Override
    public DataSource getDataSourceForFileName(File file) {
        List<DataSource> dataSourceList = dataSourceRepository.findAll();
        DataSource currentDataSource = null;
        String fileExtension = FilenameUtils.getExtension(file.getName());
        for (DataSource dataSource : dataSourceList) {
            // *_Price_*.csv = PriceDocuments
            // *_Stock_*.csv = StockDocuments
            if (StringUtils.isNotEmpty(dataSource.getFormat())) {
                if (dataSource.getFormat().equals(ServiceUtil.getFormatForTheFileExt(FilenameUtils.getExtension(file.getName()))) && file.getName().contains(dataSource.getIdentifier())) {
                    currentDataSource = dataSource;
                }
            }
        }
        return currentDataSource;
    }
}
