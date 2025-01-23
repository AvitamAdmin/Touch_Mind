package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.SavedQueryDto;
import com.touchmind.core.mongo.model.CommonFields;
import com.touchmind.core.mongo.model.SavedQuery;
import com.touchmind.core.mongo.repository.SavedQueryRepository;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.RepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return modelMapper.map(savedQueries, List.class);
    }

    public String saveSearchQuery(SavedQueryDto savedQueryDto, String source) {
        String creator = coreService.getCurrentUser().getUsername();
        savedQueryDto.setUser(creator);
        savedQueryDto.setSourceItem(source);
        SavedQuery savedQuery = savedQueryRepository.save(modelMapper.map(savedQueryDto, SavedQuery.class));
        savedQuery.setRecordId(String.valueOf(savedQuery.getId().getTimestamp()));
        savedQueryRepository.save(savedQuery);
        return "Success";
    }

    @Override
    public CommonFields validateIdentifier(String entityName, String identifier) {
        GenericImportRepository genericImportRepository = repositoryService.getRepositoryForName(entityName);
        return genericImportRepository.findByIdentifier(identifier);
    }
}
