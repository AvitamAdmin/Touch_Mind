package com.touchmind.web.controllers.admin.subsidiary;

import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.dto.SubsidiaryDto;
import com.touchmind.core.mongo.dto.SubsidiaryWsDto;
import com.touchmind.core.mongo.model.Subsidiary;
import com.touchmind.core.mongo.repository.CountryRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SubsidiaryRepository;
import com.touchmind.core.service.SubsidiaryService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/subsidiary")
public class SubsidiaryController extends BaseController {

    public static final String ADMIN_SUBSIDIARY = "/admin/subsidiary";

    Logger logger = LoggerFactory.getLogger(SubsidiaryController.class);
    @Autowired
    private SubsidiaryRepository subsidiaryRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private SubsidiaryService subsidiaryService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseBody
    public SubsidiaryWsDto getAllSubsidiaries(@RequestBody SubsidiaryWsDto subsidiaryWsDto) {
        Pageable pageable = getPageable(subsidiaryWsDto.getPage(), subsidiaryWsDto.getSizePerPage(), subsidiaryWsDto.getSortDirection(), subsidiaryWsDto.getSortField());
        SubsidiaryDto subsidiaryDto = CollectionUtils.isNotEmpty(subsidiaryWsDto.getSubsidiaries()) ? subsidiaryWsDto.getSubsidiaries().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(subsidiaryDto, subsidiaryWsDto.getOperator());
        Subsidiary subsidiary = subsidiaryDto != null ? modelMapper.map(subsidiaryDto, Subsidiary.class) : null;
        Page<Subsidiary> page = isSearchActive(subsidiary) != null ? subsidiaryRepository.findAll(Example.of(subsidiary, exampleMatcher), pageable) : subsidiaryRepository.findAll(pageable);
        subsidiaryWsDto.setSubsidiaries(modelMapper.map(page.getContent(), List.class));
        subsidiaryWsDto.setBaseUrl(ADMIN_SUBSIDIARY);
        subsidiaryWsDto.setTotalPages(page.getTotalPages());
        subsidiaryWsDto.setTotalRecords(page.getTotalElements());
        subsidiaryWsDto.setAttributeList(getConfiguredAttributes(subsidiaryWsDto.getNode()));
        return subsidiaryWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Subsidiary());
    }

    @GetMapping("/get")
    @ResponseBody
    public SubsidiaryWsDto getActiveSubsidiaries() {
        SubsidiaryWsDto subsidiaryWsDto = new SubsidiaryWsDto();
        subsidiaryWsDto.setSubsidiaries(modelMapper.map(subsidiaryRepository.findByStatusOrderByIdentifier(true), List.class));
        subsidiaryWsDto.setBaseUrl(ADMIN_SUBSIDIARY);
        return subsidiaryWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public SubsidiaryWsDto handleEdit(@RequestBody SubsidiaryWsDto request) {
        return subsidiaryService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public SubsidiaryWsDto addSubsidiary() {
        SubsidiaryWsDto subsidiaryWsDto = new SubsidiaryWsDto();
        subsidiaryWsDto.setBaseUrl(ADMIN_SUBSIDIARY);
        subsidiaryWsDto.setCountries(modelMapper.map(countryRepository.findByStatusOrderByIdentifier(true), List.class));
        return subsidiaryWsDto;
    }

    @PostMapping("/delete")
    public SubsidiaryWsDto deleteSubsidiary(@RequestBody SubsidiaryWsDto subsidiaryWsDto) {
        for (SubsidiaryDto subsidiaryDto : subsidiaryWsDto.getSubsidiaries()) {
            subsidiaryRepository.deleteByRecordId(subsidiaryDto.getRecordId());
        }
        subsidiaryWsDto.setMessage("Data deleted Successfully!!");
        subsidiaryWsDto.setBaseUrl(ADMIN_SUBSIDIARY);
        return subsidiaryWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public SubsidiaryWsDto editMultiple(@RequestBody SubsidiaryWsDto request) {
        SubsidiaryWsDto subsidiaryWsDto = new SubsidiaryWsDto();
        List<Subsidiary> subsidiaries = new ArrayList<>();
        for (SubsidiaryDto subsidiaryDto : request.getSubsidiaries()) {
            subsidiaries.add(subsidiaryRepository.findByRecordId(subsidiaryDto.getRecordId()));
        }
        subsidiaryWsDto.setSubsidiaries(modelMapper.map(subsidiaries, List.class));
        subsidiaryWsDto.setCountries(modelMapper.map(countryRepository.findByStatusOrderByIdentifier(true), List.class));
        subsidiaryWsDto.setRedirectUrl("/admin/subsidiary");
        subsidiaryWsDto.setBaseUrl(ADMIN_SUBSIDIARY);
        return subsidiaryWsDto;
    }

    @PostMapping("/upload")
    public SubsidiaryWsDto uploadFile(@RequestBody MultipartFile file) {
        SubsidiaryWsDto subsidiaryWsDto = new SubsidiaryWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.SUBSIDIARY, EntityConstants.SUBSIDIARY, subsidiaryWsDto);
            if (StringUtils.isEmpty(subsidiaryWsDto.getMessage())) {
                subsidiaryWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return subsidiaryWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public SubsidiaryWsDto uploadFile() {
        SubsidiaryWsDto subsidiaryWsDto = new SubsidiaryWsDto();
        try {
            subsidiaryWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SUBSIDIARY));
            return subsidiaryWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
