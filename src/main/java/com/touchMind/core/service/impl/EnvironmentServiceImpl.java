package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.EnvironmentDto;
import com.touchMind.core.mongo.dto.EnvironmentWsDto;
import com.touchMind.core.mongo.model.Environment;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.EnvironmentRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.EnvironmentService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnvironmentServiceImpl implements EnvironmentService {

    public static final String ADMIN_ENVIRONMENT = "/admin/environment";

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private CoreService coreService;

//    @Autowired
//    private SubsidiaryService subsidiaryService;

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
            if (environment.isAdd() && baseService.validateIdentifier(EntityConstants.ENVIRONMENT, environment.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = environmentRepository.findByIdentifier(environment.getIdentifier());
            if (requestData != null) {
                modelMapper.map(environment, requestData);
            } else {
                requestData = modelMapper.map(environment, Environment.class);
            }
            baseService.populateCommonData(requestData);
            environmentRepository.save(requestData);
            environmentList.add(requestData);
            environmentWsDto.setMessage("Environment updated successfully!!");
            environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        }
        Type listType = new TypeToken<List<EnvironmentDto>>() {
        }.getType();
        environmentWsDto.setEnvironments(modelMapper.map(environmentList, listType));
        return environmentWsDto;
    }
}
