package com.touchMind.web.controllers.admin.widget;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.WidgetDisplayTypeDto;
import com.touchMind.core.mongo.dto.WidgetDisplayTypeWsDto;
import com.touchMind.core.mongo.model.WidgetDisplayType;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.WidgetDisplayTypeRepository;
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
@RequestMapping("/admin/widgetdisplaytype")
public class WidgetDisplayTypeController extends BaseController {

    public static final String ADMIN_WIDGET = "/admin/widgetdisplaytype";

    Logger logger = LoggerFactory.getLogger(WidgetDisplayTypeController.class);

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @Autowired
    private WidgetDisplayTypeRepository widgetDisplayTypeRepository;

    @PostMapping
    @ResponseBody
    public WidgetDisplayTypeWsDto getAll(@RequestBody WidgetDisplayTypeWsDto widgetDisplayTypeWsDto) {
        Pageable pageable = getPageable(widgetDisplayTypeWsDto.getPage(), widgetDisplayTypeWsDto.getSizePerPage(), widgetDisplayTypeWsDto.getSortDirection(), widgetDisplayTypeWsDto.getSortField());
        WidgetDisplayTypeDto widgetDisplayTypeDto = CollectionUtils.isNotEmpty(widgetDisplayTypeWsDto.getWidgetDisplayTypes()) ? widgetDisplayTypeWsDto.getWidgetDisplayTypes().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(widgetDisplayTypeDto, widgetDisplayTypeWsDto.getOperator());
        WidgetDisplayType widgetDisplayType = widgetDisplayTypeDto != null ? modelMapper.map(widgetDisplayTypeDto, WidgetDisplayType.class) : null;
        Page<WidgetDisplayType> page = isSearchActive(widgetDisplayType) != null ? widgetDisplayTypeRepository.findAll(Example.of(widgetDisplayType, exampleMatcher), pageable) : widgetDisplayTypeRepository.findAll(pageable);
        Type listType = new TypeToken<List<WidgetDisplayTypeDto>>() {
        }.getType();
        widgetDisplayTypeWsDto.setWidgetDisplayTypes(modelMapper.map(page.getContent(), listType));
        widgetDisplayTypeWsDto.setBaseUrl(ADMIN_WIDGET);
        widgetDisplayTypeWsDto.setTotalPages(page.getTotalPages());
        widgetDisplayTypeWsDto.setTotalRecords(page.getTotalElements());
        widgetDisplayTypeWsDto.setAttributeList(getConfiguredAttributes(widgetDisplayTypeWsDto.getNode()));
        widgetDisplayTypeWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.WIDGET_DISPLAY_TYPE));
        return widgetDisplayTypeWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody WidgetDisplayTypeWsDto widgetDisplayTypeWsDto) {
        return getConfiguredAttributes(widgetDisplayTypeWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new WidgetDisplayType());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.WIDGET_DISPLAY_TYPE);
    }

    @GetMapping("/get")
    @ResponseBody
    public WidgetDisplayTypeWsDto getActiveData() {
        WidgetDisplayTypeWsDto widgetDisplayTypeWsDto = new WidgetDisplayTypeWsDto();
        Type listType = new TypeToken<List<WidgetDisplayTypeDto>>() {
        }.getType();
        widgetDisplayTypeWsDto.setWidgetDisplayTypes(modelMapper.map(widgetDisplayTypeRepository.findByStatusOrderByIdentifier(true), listType));
        widgetDisplayTypeWsDto.setBaseUrl(ADMIN_WIDGET);
        return widgetDisplayTypeWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody WidgetDisplayTypeDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(widgetDisplayTypeRepository.findByIdentifier(recordId), WidgetDisplayTypeDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public WidgetDisplayTypeWsDto handleEdit(@RequestBody WidgetDisplayTypeWsDto request) {
        WidgetDisplayTypeWsDto widgetDisplayTypeWsDto = new WidgetDisplayTypeWsDto();
        List<WidgetDisplayTypeDto> widgetDisplayTypes = request.getWidgetDisplayTypes();
        List<WidgetDisplayType> displayTypes = new ArrayList<>();
        WidgetDisplayType requestData = null;
        for (WidgetDisplayTypeDto widgetDisplayTypeDto : widgetDisplayTypes) {
            if (widgetDisplayTypeDto.isAdd() && baseService.validateIdentifier(EntityConstants.WIDGET_DISPLAY_TYPE, widgetDisplayTypeDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            if (widgetDisplayTypeDto.getIdentifier() != null) {
                requestData = widgetDisplayTypeRepository.findByIdentifier(widgetDisplayTypeDto.getIdentifier());
                modelMapper.map(widgetDisplayTypeDto, requestData);
            } else {

                requestData = modelMapper.map(widgetDisplayTypeDto, WidgetDisplayType.class);
            }
            baseService.populateCommonData(requestData);
            widgetDisplayTypeRepository.save(requestData);
            displayTypes.add(requestData);
            widgetDisplayTypeWsDto.setBaseUrl(ADMIN_WIDGET);
        }
        Type listType = new TypeToken<List<WidgetDisplayTypeDto>>() {
        }.getType();
        widgetDisplayTypeWsDto.setWidgetDisplayTypes(modelMapper.map(displayTypes, listType));
        widgetDisplayTypeWsDto.setMessage("Widget Type updated successfully!!");
        return widgetDisplayTypeWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public WidgetDisplayTypeWsDto add() {
        WidgetDisplayTypeWsDto widgetDisplayTypeWsDto = new WidgetDisplayTypeWsDto();
        widgetDisplayTypeWsDto.setBaseUrl(ADMIN_WIDGET);
        return widgetDisplayTypeWsDto;
    }

    @PostMapping("/delete")
    public WidgetDisplayTypeWsDto delete(@RequestBody WidgetDisplayTypeWsDto widgetDisplayTypeWsDto) {
        for (WidgetDisplayTypeDto widgetDisplayTypeDto : widgetDisplayTypeWsDto.getWidgetDisplayTypes()) {
            widgetDisplayTypeRepository.deleteByIdentifier(widgetDisplayTypeDto.getIdentifier());
        }
        widgetDisplayTypeWsDto.setMessage("Data deleted Successfully!!");
        widgetDisplayTypeWsDto.setBaseUrl(ADMIN_WIDGET);
        return widgetDisplayTypeWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public WidgetDisplayTypeWsDto editMultiple(@RequestBody WidgetDisplayTypeWsDto request) {
        WidgetDisplayTypeWsDto widgetDisplayTypeWsDto = new WidgetDisplayTypeWsDto();
        List<WidgetDisplayType> widgetDisplayTypes = new ArrayList<>();
        for (WidgetDisplayTypeDto widgetDisplayTypeDto : request.getWidgetDisplayTypes()) {
            widgetDisplayTypes.add(widgetDisplayTypeRepository.findByIdentifier(widgetDisplayTypeDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<WidgetDisplayTypeDto>>() {
        }.getType();
        widgetDisplayTypeWsDto.setWidgetDisplayTypes(modelMapper.map(widgetDisplayTypes, listType));
        widgetDisplayTypeWsDto.setRedirectUrl(ADMIN_WIDGET);
        widgetDisplayTypeWsDto.setBaseUrl(ADMIN_WIDGET);
        return widgetDisplayTypeWsDto;
    }

    @PostMapping("/upload")
    public WidgetDisplayTypeWsDto uploadFile(@RequestBody MultipartFile file) {
        WidgetDisplayTypeWsDto widgetDisplayTypeWsDto = new WidgetDisplayTypeWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.WIDGET_DISPLAY_TYPE, EntityConstants.WIDGET_DISPLAY_TYPE, widgetDisplayTypeWsDto);
            if (StringUtils.isEmpty(widgetDisplayTypeWsDto.getMessage())) {
                widgetDisplayTypeWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return widgetDisplayTypeWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public WidgetDisplayTypeWsDto uploadFile(@RequestBody WidgetDisplayTypeWsDto widgetDisplayTypeWsDto) {

        try {
            widgetDisplayTypeWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.WIDGET_DISPLAY_TYPE, widgetDisplayTypeWsDto.getHeaderFields()));
            return widgetDisplayTypeWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
