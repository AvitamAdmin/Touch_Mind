package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.EnvironmentDto;
import com.cheil.core.mongo.dto.EnvironmentWsDto;
import com.cheil.core.mongo.model.Environment;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.EnvironmentRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.EnvironmentService;
import com.cheil.core.service.SubsidiaryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnvironmentServiceImpl implements EnvironmentService {

    public static final String ADMIN_ENVIRONMENT = "/admin/environment";

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private CoreService coreService;

    @Autowired
    private SubsidiaryService subsidiaryService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @Override
    public EnvironmentWsDto handelEdit(EnvironmentWsDto request) {
        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
        environmentWsDto.setExistingEnvironmentCount(0);
        Environment requestData = null;
        List<EnvironmentDto> environments = request.getEnvironments();
        List<Environment> environmentList = new ArrayList<>();
        for (EnvironmentDto environment : environments) {
            if (environment.getRecordId() != null) {
                requestData = environmentRepository.findByRecordId(environment.getRecordId());
                modelMapper.map(environment, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.ENVIRONMENT, environment.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(environment, Environment.class);
            }
            baseService.populateCommonData(requestData);
            environmentRepository.save(requestData);
            if (environment.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            environmentRepository.save(requestData);
            environmentList.add(requestData);
            environmentWsDto.setMessage("Environment was updated successfully!!");
            environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        }
        environmentWsDto.setEnvironments(modelMapper.map(environmentList, List.class));
        return environmentWsDto;
    }
}
