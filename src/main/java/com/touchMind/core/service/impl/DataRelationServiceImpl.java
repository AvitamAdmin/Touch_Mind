package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.DataRelationDto;
import com.touchMind.core.mongo.dto.DataRelationParamsDto;
import com.touchMind.core.mongo.dto.DataRelationWsDto;
import com.touchMind.core.mongo.model.DataRelation;
import com.touchMind.core.mongo.model.DataRelationParams;
import com.touchMind.core.mongo.repository.DataRelationParamsRepository;
import com.touchMind.core.mongo.repository.DataRelationRepository;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.DataRelationService;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataRelationServiceImpl implements DataRelationService {

    public static final String ADMIN_DATA_RELATION = "/admin/dataRelation";

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private DataRelationRepository dataRelationRepository;

    @Autowired
    private CoreService coreService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DataRelationParamsRepository dataRelationParamsRepository;

    @Autowired
    private BaseService baseService;

    @Override
    public DataRelationWsDto handelEdit(DataRelationWsDto request) {
        DataRelationWsDto dataRelationWsDto = new DataRelationWsDto();
        DataRelation requestData = null;
        List<DataRelationDto> dataRelations = request.getDataRelations();
        List<DataRelation> dataRelationList = new ArrayList<>();
        for (DataRelationDto dataRelation : dataRelations) {
            if (dataRelation.isAdd() && baseService.validateIdentifier(EntityConstants.DATA_RELATION, dataRelation.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = dataRelationRepository.findByIdentifier(dataRelation.getIdentifier());
            if (requestData != null) {
                modelMapper.map(dataRelation, requestData);
            } else {
                requestData = modelMapper.map(dataRelation, DataRelation.class);
            }
            baseService.populateCommonData(requestData);
            if (CollectionUtils.isNotEmpty(dataRelation.getDataRelationParams())) {
                List<DataRelationParams> dataRelationParamsList = new ArrayList<>();
                for (DataRelationParamsDto dataRelationParamsDto : dataRelation.getDataRelationParams()) {
                    if (dataRelationParamsDto.getIdentifier() != null) {
                        dataRelationParamsList.add(dataRelationParamsRepository.findByIdentifier(dataRelationParamsDto.getIdentifier()));
                    } else {
                        DataRelationParams dataRelationParams = modelMapper.map(dataRelationParamsDto, DataRelationParams.class);
                        dataRelationParamsRepository.save(dataRelationParams);
                        dataRelationParamsList.add(dataRelationParams);
                    }
                }
                requestData.setDataRelationParams(dataRelationParamsList);
            }
            dataRelationRepository.save(requestData);
            dataRelationList.add(requestData);
            dataRelationWsDto.setBaseUrl(ADMIN_DATA_RELATION);
            dataRelationWsDto.setMessage("DataRelation updated successfully");
        }
        Type listType = new TypeToken<List<DataRelationDto>>() {
        }.getType();
        dataRelationWsDto.setDataRelations(modelMapper.map(dataRelationList, listType));
        return dataRelationWsDto;
    }
}
