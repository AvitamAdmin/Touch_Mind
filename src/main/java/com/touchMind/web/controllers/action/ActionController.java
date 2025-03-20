package com.touchMind.web.controllers.action;

import com.touchMind.core.mongo.dto.ActionDto;
import com.touchMind.core.mongo.dto.ActionWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Action;
import com.touchMind.core.mongo.model.Catalog;
import com.touchMind.core.mongo.model.Module;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.model.SystemRole;
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
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.web.controllers.BaseController;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/action")
public class ActionController extends BaseController {

    public static final String ADMIN_ACTION = "/admin/action";
    @Autowired
    protected NodeRepository nodeRepository;
    Logger logger = LoggerFactory.getLogger(ActionController.class);
    @Autowired
    private ActionRepository actionRepository;
    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SystemRoleRepository systemRoleRepository;

//    @Autowired
//    private SubsidiaryService subsidiaryService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ActionService actionService;
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public ActionWsDto getAllActions(@RequestBody ActionWsDto actionWsDto) throws IOException {
        Pageable pageable = getPageable(actionWsDto.getPage(), actionWsDto.getSizePerPage(), actionWsDto.getSortDirection(), actionWsDto.getSortField());
        ActionDto actionDto = CollectionUtils.isNotEmpty(actionWsDto.getActions()) ? actionWsDto.getActions().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(actionDto, actionWsDto.getOperator());
        Action action = actionDto != null ? modelMapper.map(actionDto, Action.class) : null;
        Page<Action> page = action != null ? (isSearchActive(action) != null ? actionRepository.findAll(Example.of(action, exampleMatcher), pageable) : actionRepository.findAll(pageable)) : actionRepository.findAll(pageable);
        Type listType = new TypeToken<List<ActionDto>>() {
        }.getType();
        actionWsDto.setActions(modelMapper.map(page.getContent(), listType));
        actionWsDto.setBaseUrl(ADMIN_ACTION);
        actionWsDto.setTotalPages(page.getTotalPages());
        actionWsDto.setTotalRecords(page.getTotalElements());
        actionWsDto.setAttributeList(getConfiguredAttributes(actionWsDto.getNode()));
        return actionWsDto;
    }

    @GetMapping("/get")
    @ResponseBody
    public ActionWsDto getActiveSubsidiaries() {
        ActionWsDto actionWsDto = new ActionWsDto();
        actionWsDto.setBaseUrl(ADMIN_ACTION);
        Type listType = new TypeToken<List<ActionDto>>() {
        }.getType();
        actionWsDto.setActions(modelMapper.map(actionRepository.findByStatusOrderByIdentifier(true), listType));
        actionWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.ACTION));
        return actionWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody ActionWsDto actionWsDto) {
        return getConfiguredAttributes(actionWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Action());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.ACTION);
    }

    @RequestMapping(value = "/getCatalogsForSystem/{systemId}", method = RequestMethod.GET)
    public @ResponseBody List<Catalog> getSites(@PathVariable("systemId") String systemId) {
        List<Catalog> catalogList = new ArrayList<>();
        Iterable<Catalog> catalogs = catalogRepository.findAll();
        catalogs.forEach(catalogList::add);
        return catalogList.stream().filter(catalog -> catalog.getSystem() != null && catalog.getSystem().getId().equals(systemId)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/getModulesForSystem/{systemId}", method = RequestMethod.GET)
    public @ResponseBody List<Module> getModelForSubsidiary(@PathVariable("systemId") String systemId) {
        List<Module> moduleList = new ArrayList<>();
        Iterable<Module> modules = moduleRepository.findAll();
        modules.forEach(moduleList::add);
        return moduleList.stream().filter(module -> module.getSystem() != null && module.getSystem().getId().equals(systemId)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/getRolesForSystem/{systemId}", method = RequestMethod.GET)
    public @ResponseBody List<SystemRole> getRolesForSystem(@PathVariable("systemId") String systemId) {
        List<SystemRole> roleList = new ArrayList<>();
        Iterable<SystemRole> roles = systemRoleRepository.findAll();
        roles.forEach(roleList::add);
        return roleList.stream().filter(role -> role.getSystem() != null && role.getSystem().getId().equals(systemId)).collect(Collectors.toList());
    }

    @PostMapping("/getedits")
    @ResponseBody
    public ActionWsDto editsAction(@RequestBody ActionWsDto request) throws IOException {
        ActionWsDto actionWsDto = new ActionWsDto();
        actionWsDto.setBaseUrl(ADMIN_ACTION);
        List<Action> actions = new ArrayList<>();
        for (ActionDto actionDto : request.getActions()) {
            actions.add(actionRepository.findByIdentifier(actionDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<ActionDto>>() {
        }.getType();
        actionWsDto.setActions(modelMapper.map(actions, listType));
        actionWsDto.setRedirectUrl("/admin/action");
        return actionWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody ActionDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(actionRepository.findByIdentifier(recordId), ActionDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public ActionWsDto handleEdit(@RequestBody ActionWsDto actionWsDto) throws IOException, InterruptedException {
        return actionService.handleEdit(actionWsDto);
    }

    @GetMapping("/add")
    @ResponseBody
    public ActionWsDto addAction() {
        ActionWsDto actionWsDto = new ActionWsDto();
        Node toolkit = nodeRepository.findByPath("/toolkit");
        actionWsDto.setBaseUrl(ADMIN_ACTION);
        return actionWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public ActionWsDto deleteAction(@RequestBody ActionWsDto actionWsDto) throws IOException, InterruptedException {
        try {
            for (ActionDto actionDto : actionWsDto.getActions()) {
                actionRepository.deleteByIdentifier(actionDto.getIdentifier());
            }
        } catch (Exception e) {
            logger.error("Exception while deleting", e);
            return actionWsDto;
        }
        actionWsDto.setBaseUrl(ADMIN_ACTION);
        actionWsDto.setMessage("Data deleted successfully!!");
        return actionWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public ActionWsDto uploadFile(@RequestBody MultipartFile file) {
        ActionWsDto actionWsDto = new ActionWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.ACTION, EntityConstants.ACTION, actionWsDto);
            if (StringUtils.isEmpty(actionWsDto.getMessage())) {
                actionWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return actionWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public ActionWsDto uploadFile(@RequestBody ActionWsDto actionWsDto) {

        try {
            actionWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.ACTION, actionWsDto.getHeaderFields()));
            return actionWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
