package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.model.DataSource;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.service.FileHandlerService;
import com.touchMind.core.service.ServiceUtil;
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
                if (StringUtils.equals(dataSource.getFormat(), ServiceUtil.getFormatForTheFileExt(FilenameUtils.getExtension(file.getName())))) {
                    if (StringUtils.contains(file.getName(), dataSource.getIdentifier())) {
                        currentDataSource = dataSource;
                    }
                    if (StringUtils.contains(file.getName(), dataSource.getSourceAddress())) {
                        currentDataSource = dataSource;
                    }
                }
            }
        }
        return currentDataSource;
    }
}
