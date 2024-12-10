package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.ImpactConfigDto;
import com.cheil.core.mongo.dto.ImpactConfigWsDto;
import com.cheil.core.mongo.model.ImpactConfig;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.ImpactConfigRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.ImpactConfigService;
import com.cheil.form.ImpactConfigForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (impactConfigForm.getRecordId() != null) {
            ImpactConfig impactConfigOptional = impactConfigRepository.findByRecordId(impactConfigForm.getRecordId());
            if (impactConfigOptional != null) {
                ImpactConfig impactConfig = impactConfigOptional;
                modelMapper.map(impactConfigForm, impactConfig);
                impactConfigRepository.save(impactConfig);
                return impactConfig;

            }
        } else {
            ImpactConfig impactConfig = modelMapper.map(impactConfigForm, ImpactConfig.class);
            impactConfigRepository.save(impactConfig);
            if (impactConfig.getRecordId() == null) {
                impactConfig.setRecordId(String.valueOf(impactConfig.getId().getTimestamp()));
                impactConfigRepository.save(impactConfig);
                return impactConfig;
            }
        }
        return null;
    }

    @Override
    public ImpactConfigWsDto handleEdit(ImpactConfigWsDto request) {
        ImpactConfigWsDto impactConfigWsDto = new ImpactConfigWsDto();
        List<ImpactConfigDto> impactConfigs = request.getImpactConfigs();
        List<ImpactConfig> impactConfigList = new ArrayList<>();
        ImpactConfig requestData = null;
        for (ImpactConfigDto impactConfig : impactConfigs) {
            if (impactConfig.getRecordId() != null) {
                requestData = impactConfigRepository.findByRecordId(impactConfig.getRecordId());
                modelMapper.map(impactConfig, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.IMPACT_CONFIG, impactConfig.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(impactConfig, ImpactConfig.class);
            }
            baseService.populateCommonData(requestData);
            impactConfigRepository.save(requestData);
            if (impactConfig.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            impactConfigWsDto.setBaseUrl(ADMIN_IMPACTCONFIG);
            impactConfigRepository.save(requestData);
            impactConfigList.add(requestData);
        }
        impactConfigWsDto.setImpactConfigs(modelMapper.map(impactConfigList, List.class));
        return impactConfigWsDto;
    }
}
