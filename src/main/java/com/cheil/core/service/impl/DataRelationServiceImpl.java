package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.DataRelationDto;
import com.cheil.core.mongo.dto.DataRelationParamsDto;
import com.cheil.core.mongo.dto.DataRelationWsDto;
import com.cheil.core.mongo.model.DataRelation;
import com.cheil.core.mongo.model.DataRelationParams;
import com.cheil.core.mongo.repository.DataRelationParamsRepository;
import com.cheil.core.mongo.repository.DataRelationRepository;
import com.cheil.core.mongo.repository.DataSourceRepository;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.DataRelationService;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if (dataRelation.getRecordId() != null) {
                requestData = dataRelationRepository.findByRecordId(dataRelation.getRecordId());
                modelMapper.map(dataRelation, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.DATA_RELATION, dataRelation.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(dataRelation, DataRelation.class);
            }
            baseService.populateCommonData(requestData);
            if (CollectionUtils.isNotEmpty(dataRelation.getDataRelationParams())) {
                List<DataRelationParams> dataRelationParamsList = new ArrayList<>();
                for (DataRelationParamsDto dataRelationParamsDto : dataRelation.getDataRelationParams()) {
                    if (dataRelationParamsDto.getRecordId() != null) {
                        dataRelationParamsList.add(dataRelationParamsRepository.findByRecordId(dataRelationParamsDto.getRecordId()));
                    } else {
                        DataRelationParams dataRelationParams = modelMapper.map(dataRelationParamsDto, DataRelationParams.class);
                        dataRelationParamsRepository.save(dataRelationParams);
                        dataRelationParams.setRecordId(String.valueOf(dataRelationParams.getId().getTimestamp()));
                        dataRelationParamsRepository.save(dataRelationParams);
                        dataRelationParamsList.add(dataRelationParams);
                    }
                }
                requestData.setDataRelationParams(dataRelationParamsList);
            }
            dataRelationRepository.save(requestData);
            if (dataRelation.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            dataRelationRepository.save(requestData);
            dataRelationList.add(requestData);
            dataRelationWsDto.setBaseUrl(ADMIN_DATA_RELATION);
            dataRelationWsDto.setMessage("DataRelation was updated successfully");
        }
        dataRelationWsDto.setDataRelations(modelMapper.map(dataRelationList, List.class));
        return dataRelationWsDto;
    }
}
