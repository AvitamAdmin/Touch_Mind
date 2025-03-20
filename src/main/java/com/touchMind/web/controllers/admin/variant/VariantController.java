package com.touchMind.web.controllers.admin.variant;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.VariantDto;
import com.touchMind.core.mongo.dto.VariantWsDto;
import com.touchMind.core.mongo.model.Variant;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.VariantRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.VariantService;
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
@RequestMapping("/admin/variant")
public class VariantController extends BaseController {

    public static final String ADMIN_VARIANT = "/admin/variant";

    Logger logger = LoggerFactory.getLogger(VariantController.class);
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private VariantService variantService;
    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public VariantWsDto getAllVariants(@RequestBody VariantWsDto variantWsDto) {
        Pageable pageable = getPageable(variantWsDto.getPage(), variantWsDto.getSizePerPage(), variantWsDto.getSortDirection(), variantWsDto.getSortField());
        VariantDto variantDto = CollectionUtils.isNotEmpty(variantWsDto.getVariants()) ? variantWsDto.getVariants().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(variantDto, variantWsDto.getOperator());
        Variant variant = variantDto != null ? modelMapper.map(variantDto, Variant.class) : null;
        Page<Variant> page = isSearchActive(variant) != null ? variantRepository.findAll(Example.of(variant, exampleMatcher), pageable) : variantRepository.findAll(pageable);
        Type listType = new TypeToken<List<VariantDto>>() {
        }.getType();
        variantWsDto.setVariants(modelMapper.map(page.getContent(), listType));
        variantWsDto.setBaseUrl(ADMIN_VARIANT);
        variantWsDto.setTotalPages(page.getTotalPages());
        variantWsDto.setTotalRecords(page.getTotalElements());
        variantWsDto.setAttributeList(getConfiguredAttributes(variantWsDto.getNode()));
        variantWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.VARIANT));
        return variantWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody VariantWsDto variantWsDto) {
        return getConfiguredAttributes(variantWsDto.getNode());
    }


    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Variant());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.VARIANT);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody VariantDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(variantRepository.findByIdentifier(recordId), VariantDto.class);
    }

    @GetMapping("/get")
    @ResponseBody
    public VariantWsDto getActiveVariants() {
        VariantWsDto variantWsDto = new VariantWsDto();
        variantWsDto.setBaseUrl(ADMIN_VARIANT);
        Type listType = new TypeToken<List<VariantDto>>() {
        }.getType();
        variantWsDto.setVariants(modelMapper.map(variantRepository.findByStatusOrderByIdentifier(true), listType));
        return variantWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public VariantWsDto uploadFile(@RequestBody MultipartFile file) {
        VariantWsDto variantWsDto = new VariantWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.VARIANT, EntityConstants.VARIANT, variantWsDto);
            if (StringUtils.isEmpty(variantWsDto.getMessage())) {
                variantWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return variantWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public VariantWsDto handleEdit(@RequestBody VariantWsDto request) {
        return variantService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public VariantWsDto addCategory() {
        VariantWsDto variantWsDto = new VariantWsDto();
        variantWsDto.setBaseUrl(ADMIN_VARIANT);
        return variantWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public VariantWsDto deleteCategory(@RequestBody VariantWsDto variantWsDto) {
        for (VariantDto variantDto : variantWsDto.getVariants()) {
            variantRepository.deleteByIdentifier(variantDto.getIdentifier());
        }
        variantWsDto.setBaseUrl(ADMIN_VARIANT);
        variantWsDto.setMessage("Data deleted successfully!!");
        return variantWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public VariantWsDto editMultiple(@RequestBody VariantWsDto request) {
        VariantWsDto variantWsDto = new VariantWsDto();
        List<Variant> variants = new ArrayList<>();
        for (VariantDto variantDto : request.getVariants()) {
            variants.add(variantRepository.findByIdentifier(variantDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<VariantDto>>() {
        }.getType();
        variantWsDto.setVariants(modelMapper.map(variants, listType));
        variantWsDto.setBaseUrl(ADMIN_VARIANT);
        return variantWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public VariantWsDto uploadFile(@RequestBody VariantWsDto variantWsDto) {

        try {
            variantWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.VARIANT, variantWsDto.getHeaderFields()));
            return variantWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
