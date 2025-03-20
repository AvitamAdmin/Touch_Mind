package com.touchMind.web.controllers;

import com.touchMind.core.mongo.dto.ImpactConfigDto;
import com.touchMind.core.mongo.dto.ImpactConfigWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.ImpactConfig;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.ImpactConfigRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.DashboardProfileService;
import com.touchMind.core.service.ImpactConfigService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/admin/impactConfig")
public class ImpactConfigController extends BaseController {
    public static final String ADMIN_IMPACTCONFIG = "/admin/impactConfig";

    @Autowired
    private CoreService coreService;

    @Autowired
    private ImpactConfigRepository impactConfigRepository;

    @Autowired
    private DashboardProfileService dashboardProfileService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ImpactConfigService impactConfigService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;


    @PostMapping
    @ResponseBody
    public ImpactConfigWsDto getImpactConfigs(@RequestBody ImpactConfigWsDto impactConfigWsDto) {
        Pageable pageable = getPageable(impactConfigWsDto.getPage(), impactConfigWsDto.getSizePerPage(), impactConfigWsDto.getSortDirection(), impactConfigWsDto.getSortField());
        ImpactConfigDto impactConfigDto = CollectionUtils.isNotEmpty(impactConfigWsDto.getImpactConfigs()) ? impactConfigWsDto.getImpactConfigs().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(impactConfigDto, impactConfigWsDto.getOperator());
        ImpactConfig impactConfig = impactConfigDto != null ? modelMapper.map(impactConfigDto, ImpactConfig.class) : null;
        Page<ImpactConfig> page = isSearchActive(impactConfig) != null ? impactConfigRepository.findAll(Example.of(impactConfig, exampleMatcher), pageable) : impactConfigRepository.findAll(pageable);
        Type listType = new TypeToken<List<ImpactConfigDto>>() {
        }.getType();
        impactConfigWsDto.setImpactConfigs(modelMapper.map(page.getContent(), listType));
        impactConfigWsDto.setBaseUrl(ADMIN_IMPACTCONFIG);
        impactConfigWsDto.setTotalPages(page.getTotalPages());
        impactConfigWsDto.setTotalRecords(page.getTotalElements());
        impactConfigWsDto.setAttributeList(getConfiguredAttributes(impactConfigWsDto.getNode()));
        impactConfigWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.IMPACT_CONFIG));
        return impactConfigWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody ImpactConfigWsDto impactConfigWsDto) {
        return getConfiguredAttributes(impactConfigWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new ImpactConfig());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.IMPACT_CONFIG);
    }


    @GetMapping("/get")
    public ImpactConfigWsDto getActiveSchedulers() {
        ImpactConfigWsDto impactConfigWsDto = new ImpactConfigWsDto();
        impactConfigWsDto.setBaseUrl(ADMIN_IMPACTCONFIG);
        Type listType = new TypeToken<List<ImpactConfigDto>>() {
        }.getType();
        impactConfigWsDto.setImpactConfigs(modelMapper.map(impactConfigRepository.findByStatusOrderByIdentifier(true), listType));
        return impactConfigWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public ImpactConfigWsDto getImpactConfig() {
        ImpactConfigWsDto impactConfigWsDto = new ImpactConfigWsDto();
        impactConfigWsDto.setExistingLabelCount(0);
        impactConfigWsDto.setBaseUrl(ADMIN_IMPACTCONFIG);
        impactConfigWsDto.setDashboardLabels(dashboardProfileService.getDashboardLabels());
        return impactConfigWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public ImpactConfigWsDto addDashboardProfile(@RequestBody ImpactConfigWsDto request) {
        return impactConfigService.handleEdit(request);
    }

    @PostMapping("/getedits")
    @ResponseBody
    public ImpactConfigWsDto editMultiple(@RequestBody ImpactConfigWsDto request) {
        ImpactConfigWsDto impactConfigWsDto = new ImpactConfigWsDto();
        List<ImpactConfig> impactConfigs = new ArrayList<>();
        for (ImpactConfigDto impactConfigDto : request.getImpactConfigs()) {
            impactConfigs.add(impactConfigRepository.findByIdentifier(impactConfigDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<ImpactConfigDto>>() {
        }.getType();
        impactConfigWsDto.setImpactConfigs(modelMapper.map(impactConfigs, listType));
        impactConfigWsDto.setRedirectUrl("");
        impactConfigWsDto.setBaseUrl(ADMIN_IMPACTCONFIG);
        return impactConfigWsDto;
    }


    @PostMapping("/delete")
    @ResponseBody
    public ImpactConfigWsDto deleteDashboardProfile(@RequestBody ImpactConfigWsDto impactConfigWsDto) {
        for (ImpactConfigDto impactConfigDto : impactConfigWsDto.getImpactConfigs()) {
            impactConfigRepository.deleteByIdentifier(impactConfigDto.getIdentifier());
        }
        impactConfigWsDto.setBaseUrl(ADMIN_IMPACTCONFIG);
        impactConfigWsDto.setMessage("Data successfully deleted");
        return impactConfigWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public ImpactConfigWsDto uploadFile(@RequestBody MultipartFile file) {
        ImpactConfigWsDto impactConfigWsDto = new ImpactConfigWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.IMPACT_CONFIG, EntityConstants.IMPACT_CONFIG, impactConfigWsDto);
            if (StringUtils.isEmpty(impactConfigWsDto.getMessage())) {
                impactConfigWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return impactConfigWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public ImpactConfigWsDto uploadFile(@RequestBody ImpactConfigWsDto impactConfigWsDto) {

        try {
            impactConfigWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.IMPACT_CONFIG, impactConfigWsDto.getHeaderFields()));
            return impactConfigWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
