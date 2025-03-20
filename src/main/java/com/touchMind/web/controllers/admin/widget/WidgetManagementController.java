package com.touchMind.web.controllers.admin.widget;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.WidgetManagementDto;
import com.touchMind.core.mongo.dto.WidgetManagementWsDto;
import com.touchMind.core.mongo.model.WidgetManagement;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.WidgetManagementRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/widgetmanagement")
public class WidgetManagementController extends BaseController {

    public static final String ADMIN_WIDGET = "/admin/widgetmanagement";

    Logger logger = LoggerFactory.getLogger(WidgetManagementController.class);

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @Autowired
    private WidgetManagementRepository widgetManagementRepository;

    @PostMapping
    @ResponseBody
    public WidgetManagementWsDto getAll(@RequestBody WidgetManagementWsDto widgetManagementWsDto) {
        Pageable pageable = getPageable(widgetManagementWsDto.getPage(), widgetManagementWsDto.getSizePerPage(), widgetManagementWsDto.getSortDirection(), widgetManagementWsDto.getSortField());
        WidgetManagementDto widgetManagement = CollectionUtils.isNotEmpty(widgetManagementWsDto.getWidgetManagements()) ? widgetManagementWsDto.getWidgetManagements().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(widgetManagement, widgetManagementWsDto.getOperator());
        WidgetManagement widgetDisplayType = widgetManagement != null ? modelMapper.map(widgetManagement, WidgetManagement.class) : null;
        Page<WidgetManagement> page = isSearchActive(widgetDisplayType) != null ? widgetManagementRepository.findAll(Example.of(widgetDisplayType, exampleMatcher), pageable) : widgetManagementRepository.findAll(pageable);
        Type listType = new TypeToken<List<WidgetManagementDto>>() {
        }.getType();
        widgetManagementWsDto.setWidgetManagements(modelMapper.map(page.getContent(), listType));
        widgetManagementWsDto.setBaseUrl(ADMIN_WIDGET);
        widgetManagementWsDto.setTotalPages(page.getTotalPages());
        widgetManagementWsDto.setTotalRecords(page.getTotalElements());
        widgetManagementWsDto.setAttributeList(getConfiguredAttributes(widgetManagementWsDto.getNode()));
        widgetManagementWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.WIDGET_MANAGEMENT));
        return widgetManagementWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody WidgetManagementWsDto widgetManagementWsDto) {
        return getConfiguredAttributes(widgetManagementWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new WidgetManagement());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.WIDGET_MANAGEMENT);
    }

    @GetMapping("/get")
    @ResponseBody
    public WidgetManagementWsDto getActiveData() {
        WidgetManagementWsDto widgetManagementWsDto = new WidgetManagementWsDto();
        Type listType = new TypeToken<List<WidgetManagementDto>>() {
        }.getType();
        widgetManagementWsDto.setWidgetManagements(modelMapper.map(widgetManagementRepository.findByStatusOrderByIdentifier(true), listType));
        widgetManagementWsDto.setBaseUrl(ADMIN_WIDGET);
        return widgetManagementWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody WidgetManagementDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(widgetManagementRepository.findByIdentifier(recordId), WidgetManagementDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public WidgetManagementWsDto handleEdit(@RequestBody WidgetManagementWsDto request) {
        WidgetManagementWsDto widgetManagementWsDto = new WidgetManagementWsDto();
        List<WidgetManagementDto> widgetDisplayTypes = request.getWidgetManagements();
        List<WidgetManagement> displayTypes = new ArrayList<>();
        WidgetManagement requestData = null;
        for (WidgetManagementDto widgetManagement : widgetDisplayTypes) {
            if (widgetManagement.isAdd() && baseService.validateIdentifier(EntityConstants.WIDGET_MANAGEMENT, widgetManagement.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            if (widgetManagement.getIdentifier() != null) {
                requestData = widgetManagementRepository.findByIdentifier(widgetManagement.getIdentifier());
                modelMapper.map(widgetManagement, requestData);
            } else {
                requestData = modelMapper.map(widgetManagement, WidgetManagement.class);
            }
            baseService.populateCommonData(requestData);
            widgetManagementRepository.save(requestData);
            displayTypes.add(requestData);
            widgetManagementWsDto.setBaseUrl(ADMIN_WIDGET);
        }
        Type listType = new TypeToken<List<WidgetManagementDto>>() {
        }.getType();
        widgetManagementWsDto.setWidgetManagements(modelMapper.map(displayTypes, listType));
        widgetManagementWsDto.setMessage("Dashboard report updated successfully!!");
        return widgetManagementWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public WidgetManagementWsDto add() {
        WidgetManagementWsDto widgetManagementWsDto = new WidgetManagementWsDto();
        widgetManagementWsDto.setBaseUrl(ADMIN_WIDGET);
        return widgetManagementWsDto;
    }

    @PostMapping("/delete")
    public WidgetManagementWsDto delete(@RequestBody WidgetManagementWsDto widgetManagementWsDto) {
        for (WidgetManagementDto widgetManagement : widgetManagementWsDto.getWidgetManagements()) {
            widgetManagementRepository.deleteByIdentifier(widgetManagement.getIdentifier());
        }
        widgetManagementWsDto.setMessage("Data deleted Successfully!!");
        widgetManagementWsDto.setBaseUrl(ADMIN_WIDGET);
        return widgetManagementWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public WidgetManagementWsDto editMultiple(@RequestBody WidgetManagementWsDto request) {
        WidgetManagementWsDto widgetManagementWsDto = new WidgetManagementWsDto();
        List<WidgetManagement> widgetDisplayTypes = new ArrayList<>();
        for (WidgetManagementDto widgetManagementDto : request.getWidgetManagements()) {
            widgetDisplayTypes.add(widgetManagementRepository.findByIdentifier(widgetManagementDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<WidgetManagementDto>>() {
        }.getType();
        widgetManagementWsDto.setWidgetManagements(modelMapper.map(widgetDisplayTypes, listType));
        widgetManagementWsDto.setRedirectUrl(ADMIN_WIDGET);
        widgetManagementWsDto.setBaseUrl(ADMIN_WIDGET);
        return widgetManagementWsDto;
    }

    @PostMapping("/upload")
    public WidgetManagementWsDto uploadFile(@RequestBody MultipartFile file) {
        WidgetManagementWsDto widgetManagementWsDto = new WidgetManagementWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.WIDGET_MANAGEMENT, EntityConstants.WIDGET_MANAGEMENT, widgetManagementWsDto);
            if (StringUtils.isEmpty(widgetManagementWsDto.getMessage())) {
                widgetManagementWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return widgetManagementWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public WidgetManagementWsDto uploadFile(@RequestBody WidgetManagementWsDto widgetManagementWsDto) {

        try {
            widgetManagementWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.WIDGET_MANAGEMENT, widgetManagementWsDto.getHeaderFields()));
            return widgetManagementWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
