package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.SourceTargetMappingDto;
import com.touchMind.core.mongo.dto.SourceTargetMappingWsDto;
import com.touchMind.core.mongo.model.SourceTargetMapping;
import com.touchMind.core.mongo.repository.DataRelationRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.SourceTargetMappingService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class SourceTargetMappingServiceImpl implements SourceTargetMappingService {

    public static final String ADMIN_MAPPING = "/admin/mapping";
//    @Autowired
//    private SubsidiaryService subsidiaryService;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private DataRelationRepository dataRelationRepository;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;
    @Autowired
    private CoreService coreService;
    @Autowired
    private ModelMapper modelMapper;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private BaseService baseService;

    @Override
    public SourceTargetMappingWsDto handleEdit(SourceTargetMappingWsDto request) {
        SourceTargetMappingWsDto sourceTargetMappingWsDto = new SourceTargetMappingWsDto();
        List<SourceTargetMappingDto> sourceTargetMappings = request.getSourceTargetMappings();
        List<SourceTargetMapping> sourceTargetMappingList = new ArrayList<>();
        SourceTargetMapping requestData = null;
        for (SourceTargetMappingDto sourceTargetMapping : sourceTargetMappings) {
            if (sourceTargetMapping.isAdd() && baseService.validateIdentifier(EntityConstants.SOURCE_TARGET_MAPPING, sourceTargetMapping.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = sourceTargetMappingRepository.findByIdentifier(sourceTargetMapping.getIdentifier());
            if (requestData != null) {
                modelMapper.map(sourceTargetMapping, requestData);
            } else {
                requestData = modelMapper.map(sourceTargetMapping, SourceTargetMapping.class);
            }
            baseService.populateCommonData(requestData);
            sourceTargetMappingRepository.save(requestData);
            sourceTargetMappingList.add(requestData);
            sourceTargetMappingWsDto.setMessage("SourceTargetMapping was updated successfully!!");
            sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        }
        sourceTargetMappingWsDto.setMessage("Mapping updated successfully");
        Type listType = new TypeToken<List<SourceTargetMappingDto>>() {
        }.getType();
        sourceTargetMappingWsDto.setSourceTargetMappings(modelMapper.map(sourceTargetMappingList, listType));
        return sourceTargetMappingWsDto;
    }
}
