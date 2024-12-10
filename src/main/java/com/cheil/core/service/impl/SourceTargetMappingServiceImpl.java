package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.SourceTargetMappingDto;
import com.cheil.core.mongo.dto.SourceTargetMappingWsDto;
import com.cheil.core.mongo.model.SourceTargetMapping;
import com.cheil.core.mongo.repository.DataRelationRepository;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.NodeRepository;
import com.cheil.core.mongo.repository.SourceTargetMappingRepository;
import com.cheil.core.mongo.repository.SubsidiaryRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.SourceTargetMappingService;
import com.cheil.core.service.SubsidiaryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SourceTargetMappingServiceImpl implements SourceTargetMappingService {

    public static final String ADMIN_MAPPING = "/admin/mapping";
    @Autowired
    private SubsidiaryService subsidiaryService;
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
    @Autowired
    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private BaseService baseService;

    @Override
    public SourceTargetMappingWsDto handleEdit(SourceTargetMappingWsDto request) {
        SourceTargetMappingWsDto sourceTargetMappingWsDto = new SourceTargetMappingWsDto();
        List<SourceTargetMappingDto> sourceTargetMappings = request.getSourceTargetMappings();
        List<SourceTargetMapping> sourceTargetMappingList = new ArrayList<>();
        SourceTargetMapping requestData = null;
        for (SourceTargetMappingDto sourceTargetMapping : sourceTargetMappings) {
            if (sourceTargetMapping.getRecordId() != null) {
                requestData = sourceTargetMappingRepository.findByRecordId(sourceTargetMapping.getRecordId());
                modelMapper.map(sourceTargetMapping, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.SOURCE_TARGET_MAPPING, sourceTargetMapping.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(sourceTargetMapping, SourceTargetMapping.class);
            }
            baseService.populateCommonData(requestData);
            sourceTargetMappingRepository.save(requestData);
            if (sourceTargetMapping.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            sourceTargetMappingRepository.save(requestData);
            sourceTargetMappingList.add(requestData);
            sourceTargetMappingWsDto.setMessage("SourceTargetMapping was updated successfully!!");
            sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        }
        sourceTargetMappingWsDto.setSourceTargetMappings(modelMapper.map(sourceTargetMappingList, List.class));
        return sourceTargetMappingWsDto;
    }
}
