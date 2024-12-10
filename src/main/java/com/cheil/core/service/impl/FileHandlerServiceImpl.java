package com.cheil.core.service.impl;

import com.cheil.core.mongo.model.DataSource;
import com.cheil.core.mongo.repository.DataSourceRepository;
import com.cheil.core.service.FileHandlerService;
import com.cheil.core.service.ServiceUtil;
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
