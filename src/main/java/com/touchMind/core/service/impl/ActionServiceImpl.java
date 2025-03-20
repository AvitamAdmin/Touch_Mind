package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.ActionDto;
import com.touchMind.core.mongo.dto.ActionWsDto;
import com.touchMind.core.mongo.model.Action;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.repository.ActionRepository;
import com.touchMind.core.mongo.repository.CatalogRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.MediaRepository;
import com.touchMind.core.mongo.repository.ModuleRepository;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.SystemRepository;
import com.touchMind.core.mongo.repository.SystemRoleRepository;
import com.touchMind.core.service.ActionService;
import com.touchMind.core.service.BaseService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActionServiceImpl implements ActionService {

    public static final String ADMIN_ACTION = "/admin/action";

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CatalogRepository catalogRepository;

//    @Autowired
//    private SubsidiaryService subsidiaryService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private SystemRoleRepository systemRoleRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private CoreServiceImpl coreService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @Autowired
    private SystemRepository systemRepository;

    @Override
    public ActionWsDto handleEdit(ActionWsDto request) {
        ActionWsDto actionWsDto = new ActionWsDto();
        Node toolkit = nodeRepository.findByPath("/toolkit");
        Action requestData = null;
        List<ActionDto> actions = request.getActions();
        List<Action> actionList = new ArrayList<>();
        for (ActionDto action : actions) {
            if (action.isAdd() && baseService.validateIdentifier(EntityConstants.ACTION, action.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = actionRepository.findByIdentifier(action.getIdentifier());
            if (requestData != null) {
                modelMapper.map(action, requestData);
            } else {
                requestData = modelMapper.map(action, Action.class);
            }
            if (action.getSystem() != null) {
                requestData.setSystem(systemRepository.findByIdentifier(action.getSystem().getIdentifier()));
            }
            if (action.getModule() != null) {
                requestData.setModule(moduleRepository.findByIdentifier(action.getModule().getIdentifier()));
            }
            if (action.getCatalog() != null) {
                requestData.setCatalog(catalogRepository.findByIdentifier(action.getCatalog().getIdentifier()));
            }
            baseService.populateCommonData(requestData);
            actionRepository.save(requestData);
            actionList.add(requestData);
            actionWsDto.setBaseUrl(ADMIN_ACTION);
        }
        Type listType = new TypeToken<List<ActionDto>>() {
        }.getType();
        actionWsDto.setMessage("Action updated successfully!!");
        actionWsDto.setActions(modelMapper.map(actionList, listType));
        return actionWsDto;
    }
}
