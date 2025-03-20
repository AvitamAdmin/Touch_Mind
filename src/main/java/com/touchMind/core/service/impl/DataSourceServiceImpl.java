package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.DataSourceDto;
import com.touchMind.core.mongo.dto.DataSourceInputDto;
import com.touchMind.core.mongo.dto.DataSourceWsDto;
import com.touchMind.core.mongo.model.DataSource;
import com.touchMind.core.mongo.model.DataSourceInput;
import com.touchMind.core.mongo.repository.DataSourceInputRepository;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.DataSourceService;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
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
            if (dataSource.isAdd() && baseService.validateIdentifier(EntityConstants.DATASOURCE, dataSource.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = dataSourceRepository.findByIdentifier(dataSource.getIdentifier());
            if (requestData != null) {
                modelMapper.map(dataSource, requestData);
            } else {
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
            dataSourceList.add(requestData);
            dataSourceWsDto.setBaseUrl(ADMIN_DATA_SOURCE);
            dataSourceWsDto.setMessage("DataSource updated successfully");
        }
        Type listType = new TypeToken<List<DataSourceDto>>() {
        }.getType();
        dataSourceWsDto.setDataSources(modelMapper.map(dataSourceList, listType));
        return dataSourceWsDto;
    }

    private DataSourceInput populateDataSourceInput(DataSourceInputDto dataSourceInputDto) {
        DataSourceInput dataSourceInput = null;
        if (dataSourceInputDto.getIdentifier() != null) {
            dataSourceInput = dataSourceInputRepository.findByIdentifier(dataSourceInputDto.getIdentifier());
            modelMapper.map(dataSourceInputDto, dataSourceInput);
        } else {
            dataSourceInput = modelMapper.map(dataSourceInputDto, DataSourceInput.class);
        }
        dataSourceInputRepository.save(dataSourceInput);
        return dataSourceInput;
    }
}
