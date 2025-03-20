package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.model.CommonFields;
import com.touchMind.core.mongo.model.SavedQuery;
import com.touchMind.core.mongo.repository.SavedQueryRepository;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.RepositoryService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

@Service
public class BaseServiceImpl implements BaseService {

    @Autowired
    private CoreService coreService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private SavedQueryRepository savedQueryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void populateCommonData(CommonFields requestData) {
        String creator = coreService.getCurrentUser().getUsername();
        if (requestData.getCreator() == null) {
            requestData.setCreator(creator);
        }
        if (requestData.getCreationTime() == null) {
            requestData.setCreationTime(new Date());
        }
        requestData.setModifiedBy(creator);
        requestData.setLastModified(new Date());
    }

    public List<SavedQueryDto> getSavedQuery(String source) {
        String creator = coreService.getCurrentUser().getUsername();
        List<SavedQuery> savedQueries = savedQueryRepository.findByUserAndSourceItemOrderByIdentifier(creator, source);
        Type listType = new TypeToken<List<SavedQueryDto>>() {
        }.getType();
        return modelMapper.map(savedQueries, listType);
    }

    public String saveSearchQuery(SavedQueryDto savedQueryDto, String source) {
        if (validateIdentifier(source, savedQueryDto.getIdentifier()) != null) {
            return "Identifier already present";
        }
        String creator = coreService.getCurrentUser().getUsername();
        savedQueryDto.setUser(creator);
        savedQueryDto.setSourceItem(source);
        SavedQuery savedQuery = savedQueryRepository.save(modelMapper.map(savedQueryDto, SavedQuery.class));
        savedQueryRepository.save(savedQuery);
        return "Success";
    }

    @Override
    public CommonFields validateIdentifier(String entityName, String identifier) {
        GenericImportRepository genericImportRepository = repositoryService.getRepositoryForName(entityName);
        return genericImportRepository.findByIdentifier(identifier);
    }
}
