package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.ModuleDto;
import com.touchMind.core.mongo.dto.ModuleWsDto;
import com.touchMind.core.mongo.model.Module;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.ModuleRepository;
import com.touchMind.core.mongo.repository.SystemRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.ModuleService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
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
            if (module.isAdd() && baseService.validateIdentifier(EntityConstants.MODULE, module.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = moduleRepository.findByIdentifier(module.getIdentifier());
            if (requestData != null) {
                modelMapper.map(module, requestData);
            } else {
                requestData = modelMapper.map(module, Module.class);
            }
            baseService.populateCommonData(requestData);
            if (module.getSystem() != null) {
                requestData.setSystem(systemRepository.findByIdentifier(module.getSystem().getIdentifier()));
            }
            requestData.setLastModified(new Date());
            moduleRepository.save(requestData);
            moduleList.add(requestData);
        }
        Type listType = new TypeToken<List<ModuleDto>>() {
        }.getType();
        moduleWsDto.setModules(modelMapper.map(moduleList, listType));
        moduleWsDto.setBaseUrl(ADMIN_MODULE);
        moduleWsDto.setMessage("Module updated successfully!!");
        return moduleWsDto;
    }
}
