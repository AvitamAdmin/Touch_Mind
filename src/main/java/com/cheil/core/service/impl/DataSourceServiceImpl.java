package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.DataSourceDto;
import com.cheil.core.mongo.dto.DataSourceInputDto;
import com.cheil.core.mongo.dto.DataSourceWsDto;
import com.cheil.core.mongo.model.DataSource;
import com.cheil.core.mongo.model.DataSourceInput;
import com.cheil.core.mongo.repository.DataSourceInputRepository;
import com.cheil.core.mongo.repository.DataSourceRepository;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.DataSourceService;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataSourceServiceImpl implements DataSourceService {

    public static final String ADMIN_DATA_SOURCE = "/admin/datasource";
    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private DataSourceInputRepository dataSourceInputRepository;

    @Autowired
    private CoreService coreService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @Override
    public DataSourceWsDto handleEdit(DataSourceWsDto request) {
        DataSourceWsDto dataSourceWsDto = new DataSourceWsDto();
        DataSource requestData = null;
        List<DataSourceDto> dataSources = request.getDataSources();
        List<DataSource> dataSourceList = new ArrayList<>();
        for (DataSourceDto dataSource : dataSources) {
            if (dataSource.getRecordId() != null) {
                requestData = dataSourceRepository.findByRecordId(dataSource.getRecordId());
                modelMapper.map(dataSource, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.DATASOURCE, dataSource.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(dataSource, DataSource.class);
            }
            baseService.populateCommonData(requestData);
            if (CollectionUtils.isNotEmpty(dataSource.getDataSourceInputs())) {
                List<DataSourceInput> dataSourceInputs = new ArrayList<>();
                for (DataSourceInputDto dataSourceInputDto : dataSource.getDataSourceInputs()) {
                    dataSourceInputs.add(populateDataSourceInput(dataSourceInputDto));
                }
                requestData.setDataSourceInputs(dataSourceInputs);
            }
            dataSourceRepository.save(requestData);
            if (dataSource.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            dataSourceRepository.save(requestData);
            dataSourceList.add(requestData);
            dataSourceWsDto.setBaseUrl(ADMIN_DATA_SOURCE);
            dataSourceWsDto.setMessage("DataSource was updated successfully");
        }
        dataSourceWsDto.setDataSources(modelMapper.map(dataSourceList, List.class));
        return dataSourceWsDto;
    }

    private DataSourceInput populateDataSourceInput(DataSourceInputDto dataSourceInputDto) {
        DataSourceInput dataSourceInput = null;
        if (dataSourceInputDto.getRecordId() != null) {
            dataSourceInput = dataSourceInputRepository.findByRecordId(dataSourceInputDto.getRecordId());
            modelMapper.map(dataSourceInputDto, dataSourceInput);
        } else {
            dataSourceInput = modelMapper.map(dataSourceInputDto, DataSourceInput.class);
        }
        dataSourceInputRepository.save(dataSourceInput);
        return dataSourceInput;
    }
}
