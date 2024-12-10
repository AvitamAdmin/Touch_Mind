package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.ModuleDto;
import com.cheil.core.mongo.dto.ModuleWsDto;
import com.cheil.core.mongo.model.Module;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.ModuleRepository;
import com.cheil.core.mongo.repository.SystemRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.ModuleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {
    public static final String ADMIN_MODULE = "/admin/module";
    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private CoreService coreService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @Override
    public ModuleWsDto handleEdit(ModuleWsDto request) {
        ModuleWsDto moduleWsDto = new ModuleWsDto();
        Module requestData = null;
        List<ModuleDto> modules = request.getModules();
        List<Module> moduleList = new ArrayList<>();
        for (ModuleDto module : modules) {
            if (module.getRecordId() != null) {
                requestData = moduleRepository.findByRecordId(module.getRecordId());
                modelMapper.map(module, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.MODULE, module.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(module, Module.class);
            }
            baseService.populateCommonData(requestData);
            if (module.getSystem() != null) {
                requestData.setSystem(systemRepository.findByRecordId(module.getSystem().getRecordId()));
            }
            moduleRepository.save(requestData);
            requestData.setLastModified(new Date());
            if (module.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            moduleRepository.save(requestData);
            moduleList.add(requestData);
        }
        moduleWsDto.setModules(modelMapper.map(moduleList, List.class));
        moduleWsDto.setBaseUrl(ADMIN_MODULE);
        moduleWsDto.setMessage("Module was updated successfully!!");
        return moduleWsDto;
    }
}
