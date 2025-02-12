package com.touchmind.web.controllers.admin.intface;

import com.touchmind.core.mongo.dto.InterfaceConfigDto;
import com.touchmind.core.mongo.dto.InterfaceConfigWsDto;
import com.touchmind.core.mongo.dto.SavedQueryDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.model.InterfaceConfig;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.InterfaceConfigRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
import com.touchmind.web.controllers.BaseController;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/interfaceConfig")
public class InterfaceConfigController extends BaseController {

    public static final String ADMIN_INTERFACE = "/admin/interfaceConfig";
    Logger logger = LoggerFactory.getLogger(InterfaceConfigController.class);
    @Autowired
    private InterfaceConfigRepository interfaceConfigRepository;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public InterfaceConfigWsDto getAllModels(@RequestBody InterfaceConfigWsDto interfaceConfigWsDto) {
        Pageable pageable = getPageable(interfaceConfigWsDto.getPage(), interfaceConfigWsDto.getSizePerPage(), interfaceConfigWsDto.getSortDirection(), interfaceConfigWsDto.getSortField());
        InterfaceConfigDto interfaceConfigDto = CollectionUtils.isNotEmpty(interfaceConfigWsDto.getInterfaces()) ? interfaceConfigWsDto.getInterfaces().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(interfaceConfigDto, interfaceConfigWsDto.getOperator());
        InterfaceConfig interfaceConfig = interfaceConfigDto != null ? modelMapper.map(interfaceConfigDto, InterfaceConfig.class) : null;
        Page<InterfaceConfig> page = isSearchActive(interfaceConfig) != null ? interfaceConfigRepository.findAll(Example.of(interfaceConfig, exampleMatcher), pageable) : interfaceConfigRepository.findAll(pageable);
        interfaceConfigWsDto.setInterfaces(modelMapper.map(page.getContent(), List.class));
        interfaceConfigWsDto.setBaseUrl(ADMIN_INTERFACE);
        interfaceConfigWsDto.setTotalPages(page.getTotalPages());
        interfaceConfigWsDto.setTotalRecords(page.getTotalElements());
        interfaceConfigWsDto.setAttributeList(getConfiguredAttributes(interfaceConfigWsDto.getNode()));
        interfaceConfigWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.INTERFACE_CONFIG));
        return interfaceConfigWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new InterfaceConfig());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.INTERFACE_CONFIG);
    }

    @GetMapping("/get")
    @ResponseBody
    public InterfaceConfigWsDto getActiveNodes() {
        InterfaceConfigWsDto interfaceConfigWsDto = new InterfaceConfigWsDto();
        interfaceConfigWsDto.setBaseUrl(ADMIN_INTERFACE);
        interfaceConfigWsDto.setInterfaces(modelMapper.map(interfaceConfigRepository.findByStatusOrderByIdentifier(true), List.class));
        return interfaceConfigWsDto;
    }

    @RequestMapping(value = "/getByRecordId", method = RequestMethod.GET)
    public @ResponseBody InterfaceConfigDto getByRecordId(@RequestParam("recordId") String recordId) {
        return modelMapper.map(interfaceConfigRepository.findByRecordId(recordId), InterfaceConfigDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public InterfaceConfigWsDto handleEdit(@RequestBody InterfaceConfigWsDto request) {
        InterfaceConfigWsDto interfaceConfigWsDto = new InterfaceConfigWsDto();
        List<InterfaceConfig> interfaceConfigs = new ArrayList<>();
        for (InterfaceConfigDto interfaceConfigDto : request.getInterfaces()) {
            InterfaceConfig interfaceConfig = null;
            if (interfaceConfigDto.getRecordId() != null) {
                interfaceConfig = interfaceConfigRepository.findByRecordId(interfaceConfigDto.getRecordId());
                modelMapper.map(interfaceConfigDto, interfaceConfig);
            } else {
                if (baseService.validateIdentifier(EntityConstants.NODE, interfaceConfigDto.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                interfaceConfig = modelMapper.map(interfaceConfigDto, InterfaceConfig.class);
            }
            baseService.populateCommonData(interfaceConfig);
            interfaceConfigRepository.save(interfaceConfig);
            if (interfaceConfigDto.getRecordId() == null) {
                interfaceConfig.setRecordId(String.valueOf(interfaceConfig.getId().getTimestamp()));
            }
            interfaceConfigRepository.save(interfaceConfig);
            interfaceConfigs.add(interfaceConfig);
        }
        interfaceConfigWsDto.setInterfaces(modelMapper.map(interfaceConfigs, List.class));
        interfaceConfigWsDto.setMessage("Nodes updated successfully!!");
        interfaceConfigWsDto.setBaseUrl(ADMIN_INTERFACE);
        return interfaceConfigWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public InterfaceConfigWsDto addInterface() {
        InterfaceConfigWsDto interfaceConfigWsDto = new InterfaceConfigWsDto();
        interfaceConfigWsDto.setInterfaces(modelMapper.map(interfaceConfigRepository.findByStatusOrderByIdentifier(true), List.class));
        interfaceConfigWsDto.setBaseUrl(ADMIN_INTERFACE);
        return interfaceConfigWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public InterfaceConfigWsDto deleteInterface(@RequestBody InterfaceConfigWsDto interfaceConfigWsDto) {
        for (InterfaceConfigDto interfaceConfigDto : interfaceConfigWsDto.getInterfaces()) {
            interfaceConfigRepository.deleteByRecordId(interfaceConfigDto.getRecordId());
        }
        interfaceConfigWsDto.setBaseUrl(ADMIN_INTERFACE);
        interfaceConfigWsDto.setMessage("Data deleted successfully!!");
        return interfaceConfigWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public InterfaceConfigWsDto editMultiple(@RequestBody InterfaceConfigWsDto request) {
        InterfaceConfigWsDto interfaceConfigWsDto = new InterfaceConfigWsDto();
        List<InterfaceConfig> interfaceConfigs = new ArrayList<>();
        for (InterfaceConfigDto interfaceConfigDto : request.getInterfaces()) {
            interfaceConfigs.add(interfaceConfigRepository.findByRecordId(interfaceConfigDto.getRecordId()));
        }
        interfaceConfigWsDto.setInterfaces(modelMapper.map(interfaceConfigs, List.class));
        interfaceConfigWsDto.setBaseUrl(ADMIN_INTERFACE);
        interfaceConfigWsDto.setRedirectUrl("/admin/interfaceConfig");
        return interfaceConfigWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public InterfaceConfigWsDto uploadFile(@RequestParam("file") MultipartFile file) {
        InterfaceConfigWsDto interfaceConfigWsDto = new InterfaceConfigWsDto();
        try {
            boolean isSuccess = fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.INTERFACE_CONFIG, EntityConstants.INTERFACE_CONFIG, interfaceConfigWsDto);
            if (isSuccess) {
                if (StringUtils.isEmpty(interfaceConfigWsDto.getMessage())) {
                    interfaceConfigWsDto.setMessage("File uploaded successfully!!");
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return interfaceConfigWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public InterfaceConfigWsDto uploadFile() {
        InterfaceConfigWsDto interfaceConfigWsDto = new InterfaceConfigWsDto();
        try {
            interfaceConfigWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.INTERFACE_CONFIG));
            return interfaceConfigWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
