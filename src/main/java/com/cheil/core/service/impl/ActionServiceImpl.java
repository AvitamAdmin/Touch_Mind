package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.ActionDto;
import com.cheil.core.mongo.dto.ActionWsDto;
import com.cheil.core.mongo.model.Action;
import com.cheil.core.mongo.model.Node;
import com.cheil.core.mongo.repository.ActionRepository;
import com.cheil.core.mongo.repository.CatalogRepository;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.MediaRepository;
import com.cheil.core.mongo.repository.ModuleRepository;
import com.cheil.core.mongo.repository.NodeRepository;
import com.cheil.core.mongo.repository.SystemRepository;
import com.cheil.core.mongo.repository.SystemRoleRepository;
import com.cheil.core.service.ActionService;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.SubsidiaryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActionServiceImpl implements ActionService {

    public static final String ADMIN_ACTION = "/admin/action";

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private SubsidiaryService subsidiaryService;

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
            if (action.getRecordId() != null) {
                requestData = actionRepository.findByRecordId(action.getRecordId());
                modelMapper.map(action, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.ACTION, action.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(action, Action.class);
            }
            if (action.getSystem() != null) {
                requestData.setSystem(systemRepository.findByRecordId(action.getSystem().getRecordId()));
            }
            if (action.getModule() != null) {
                requestData.setModule(moduleRepository.findByRecordId(action.getModule().getRecordId()));
            }
            if (action.getCatalog() != null) {
                requestData.setCatalog(catalogRepository.findByRecordId(action.getCatalog().getRecordId()));
            }
            baseService.populateCommonData(requestData);
            actionRepository.save(requestData);
            if (action.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            actionRepository.save(requestData);
            actionList.add(requestData);
            actionWsDto.setBaseUrl(ADMIN_ACTION);
        }
        actionWsDto.setActions(modelMapper.map(actionList, List.class));
        return actionWsDto;
    }
}
