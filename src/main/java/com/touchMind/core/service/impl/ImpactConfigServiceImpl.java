package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.ImpactConfigDto;
import com.touchMind.core.mongo.dto.ImpactConfigWsDto;
import com.touchMind.core.mongo.model.ImpactConfig;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.ImpactConfigRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.ImpactConfigService;
import com.touchMind.form.ImpactConfigForm;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImpactConfigServiceImpl implements ImpactConfigService {

    public static final String ADMIN_IMPACTCONFIG = "/admin/impactConfig";
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ImpactConfigRepository impactConfigRepository;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;

    @Override
    public ImpactConfig saveConfig(ImpactConfigForm impactConfigForm) {
        ImpactConfig impactConfigOptional = impactConfigRepository.findByIdentifier(impactConfigForm.getIdentifier());
        if (impactConfigOptional != null) {
            ImpactConfig impactConfig = impactConfigOptional;
            modelMapper.map(impactConfigForm, impactConfig);
            impactConfigRepository.save(impactConfig);
            return impactConfig;

        } else {
            ImpactConfig impactConfig = modelMapper.map(impactConfigForm, ImpactConfig.class);
            impactConfigRepository.save(impactConfig);
            return impactConfig;

        }
    }

    @Override
    public ImpactConfigWsDto handleEdit(ImpactConfigWsDto request) {
        ImpactConfigWsDto impactConfigWsDto = new ImpactConfigWsDto();
        List<ImpactConfigDto> impactConfigs = request.getImpactConfigs();
        List<ImpactConfig> impactConfigList = new ArrayList<>();
        ImpactConfig requestData = null;
        for (ImpactConfigDto impactConfig : impactConfigs) {
            if (impactConfig.isAdd() && baseService.validateIdentifier(EntityConstants.IMPACT_CONFIG, impactConfig.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            if (impactConfig.getIdentifier() != null) {
                requestData = impactConfigRepository.findByIdentifier(impactConfig.getIdentifier());
                modelMapper.map(impactConfig, requestData);
            } else {
                requestData = modelMapper.map(impactConfig, ImpactConfig.class);
            }
            baseService.populateCommonData(requestData);
            impactConfigWsDto.setBaseUrl(ADMIN_IMPACTCONFIG);
            impactConfigRepository.save(requestData);
            impactConfigList.add(requestData);
        }
        Type listType = new TypeToken<List<ImpactConfigDto>>() {
        }.getType();
        impactConfigWsDto.setImpactConfigs(modelMapper.map(impactConfigList, listType));
        impactConfigWsDto.setMessage("Impact Config updated successfully");
        return impactConfigWsDto;
    }
}
