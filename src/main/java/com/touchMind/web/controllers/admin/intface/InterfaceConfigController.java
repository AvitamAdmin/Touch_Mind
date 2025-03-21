package com.touchMind.web.controllers.admin.intface;

import com.touchMind.core.mongo.dto.InterfaceConfigDto;
import com.touchMind.core.mongo.dto.InterfaceConfigWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.InterfaceConfig;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.InterfaceConfigRepository;
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
        Type listType = new TypeToken<List<InterfaceConfigDto>>() {
        }.getType();
        interfaceConfigWsDto.setInterfaces(modelMapper.map(page.getContent(), listType));
        interfaceConfigWsDto.setBaseUrl(ADMIN_INTERFACE);
        interfaceConfigWsDto.setTotalPages(page.getTotalPages());
        interfaceConfigWsDto.setTotalRecords(page.getTotalElements());
        interfaceConfigWsDto.setAttributeList(getConfiguredAttributes(interfaceConfigWsDto.getNode()));
        interfaceConfigWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.INTERFACE_CONFIG));
        return interfaceConfigWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody InterfaceConfigWsDto interfaceConfigWsDto) {
        return getConfiguredAttributes(interfaceConfigWsDto.getNode());
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
        Type listType = new TypeToken<List<InterfaceConfigDto>>() {
        }.getType();
        interfaceConfigWsDto.setInterfaces(modelMapper.map(interfaceConfigRepository.findByStatusOrderByIdentifier(true), listType));
        return interfaceConfigWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody InterfaceConfigDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(interfaceConfigRepository.findByIdentifier(recordId), InterfaceConfigDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public InterfaceConfigWsDto handleEdit(@RequestBody InterfaceConfigWsDto request) {
        InterfaceConfigWsDto interfaceConfigWsDto = new InterfaceConfigWsDto();
        List<InterfaceConfig> interfaceConfigs = new ArrayList<>();
        for (InterfaceConfigDto interfaceConfigDto : request.getInterfaces()) {
            InterfaceConfig interfaceConfig = null;
            if (interfaceConfigDto.getIdentifier() != null) {
                interfaceConfig = interfaceConfigRepository.findByIdentifier(interfaceConfigDto.getIdentifier());
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
            if (interfaceConfigDto.getIdentifier() == null) {
                interfaceConfig.setIdentifier(String.valueOf(interfaceConfig.getId().getTimestamp()));
            }
            interfaceConfigRepository.save(interfaceConfig);
            interfaceConfigs.add(interfaceConfig);
        }
        Type listType = new TypeToken<List<InterfaceConfigDto>>() {
        }.getType();
        interfaceConfigWsDto.setInterfaces(modelMapper.map(interfaceConfigs, listType));
        interfaceConfigWsDto.setMessage("Interface config updated successfully!!");
        interfaceConfigWsDto.setBaseUrl(ADMIN_INTERFACE);
        return interfaceConfigWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public InterfaceConfigWsDto addInterface() {
        InterfaceConfigWsDto interfaceConfigWsDto = new InterfaceConfigWsDto();
        Type listType = new TypeToken<List<InterfaceConfigDto>>() {
        }.getType();
        interfaceConfigWsDto.setInterfaces(modelMapper.map(interfaceConfigRepository.findByStatusOrderByIdentifier(true), listType));
        interfaceConfigWsDto.setBaseUrl(ADMIN_INTERFACE);
        return interfaceConfigWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public InterfaceConfigWsDto deleteInterface(@RequestBody InterfaceConfigWsDto interfaceConfigWsDto) {
        for (InterfaceConfigDto interfaceConfigDto : interfaceConfigWsDto.getInterfaces()) {
            interfaceConfigRepository.deleteByIdentifier(interfaceConfigDto.getIdentifier());
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
            interfaceConfigs.add(interfaceConfigRepository.findByIdentifier(interfaceConfigDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<InterfaceConfigDto>>() {
        }.getType();
        interfaceConfigWsDto.setInterfaces(modelMapper.map(interfaceConfigs, listType));
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

    @PostMapping("/export")
    @ResponseBody
    public InterfaceConfigWsDto uploadFile(@RequestBody InterfaceConfigWsDto interfaceConfigWsDto) {

        try {
            interfaceConfigWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.INTERFACE_CONFIG, interfaceConfigWsDto.getHeaderFields()));
            return interfaceConfigWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
