package com.touchmind.web.controllers.admin.site;

import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.dto.SiteDto;
import com.touchmind.core.mongo.dto.SiteWsDto;
import com.touchmind.core.mongo.model.Site;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SiteRepository;
import com.touchmind.core.service.SiteService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/site")
public class SiteController extends BaseController {

    public static final String ADMIN_SITE = "/admin/site";
    Logger logger = LoggerFactory.getLogger(SiteController.class);
    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SubsidiaryService subsidiaryService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseBody
    public SiteWsDto getAllSites(@RequestBody SiteWsDto siteWsDto) {
        Pageable pageable = getPageable(siteWsDto.getPage(), siteWsDto.getSizePerPage(), siteWsDto.getSortDirection(), siteWsDto.getSortField());
        SiteDto siteDto = CollectionUtils.isNotEmpty(siteWsDto.getSites()) ? siteWsDto.getSites().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(siteDto, siteWsDto.getOperator());
        Site site = siteDto != null ? modelMapper.map(siteDto, Site.class) : null;
        Page<Site> page = isSearchActive(site) != null ? siteRepository.findAll(Example.of(site, exampleMatcher), pageable) : siteRepository.findAll(pageable);
        siteWsDto.setSites(modelMapper.map(page.getContent(), List.class));
        siteWsDto.setBaseUrl(ADMIN_SITE);
        siteWsDto.setTotalPages(page.getTotalPages());
        siteWsDto.setTotalRecords(page.getTotalElements());
        siteWsDto.setAttributeList(getConfiguredAttributes(siteWsDto.getNode()));
        return siteWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Site());
    }

    @GetMapping("/get")
    @ResponseBody
    public SiteWsDto getActiveSites() {
        SiteWsDto siteWsDto = new SiteWsDto();
        siteWsDto.setSites(modelMapper.map(siteRepository.findByStatusOrderByIdentifier(true), List.class));
        siteWsDto.setBaseUrl(ADMIN_SITE);
        return siteWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public SiteWsDto handleEdit(@RequestBody SiteWsDto request) {
        return siteService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public SiteWsDto addUser() {
        SiteWsDto siteWsDto = new SiteWsDto();
        siteWsDto.setBaseUrl(ADMIN_SITE);
        return siteWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public SiteWsDto deleteSite(@RequestBody SiteWsDto siteWsDto) {
        for (SiteDto siteDto : siteWsDto.getSites()) {
            //TODO Check if this is correctly fetching the data
            siteRepository.deleteByRecordId(siteDto.getRecordId());
        }
        siteWsDto.setMessage("Data deleted successfully!!");
        siteWsDto.setBaseUrl(ADMIN_SITE);
        return siteWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public SiteWsDto editMultiple(@RequestBody SiteWsDto request) {
        SiteWsDto siteWsDto = new SiteWsDto();
        List<Site> sites = new ArrayList<>();
        for (SiteDto siteDto : request.getSites()) {
            sites.add(siteRepository.findByRecordId(siteDto.getRecordId()));
        }
        siteWsDto.setSites(modelMapper.map(sites, List.class));
        siteWsDto.setRedirectUrl("/admin/site");
        siteWsDto.setBaseUrl(ADMIN_SITE);
        return siteWsDto;
    }


    @PostMapping("/upload")
    @ResponseBody
    public SiteWsDto uploadFile(@RequestParam("file") MultipartFile file) {
        SiteWsDto siteWsDto = new SiteWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.SITE, EntityConstants.SITE, siteWsDto);
            if (StringUtils.isEmpty(siteWsDto.getMessage())) {
                siteWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return siteWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public SiteWsDto uploadFile() {
        SiteWsDto siteWsDto = new SiteWsDto();
        try {
            siteWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SITE));
            return siteWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
