package com.touchMind.web.controllers.admin.site;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.SiteDto;
import com.touchMind.core.mongo.dto.SiteWsDto;
import com.touchMind.core.mongo.model.Site;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.SiteService;
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
@RequestMapping("/admin/site")
public class SiteController extends BaseController {

    public static final String ADMIN_SITE = "/admin/site";
    Logger logger = LoggerFactory.getLogger(SiteController.class);
    @Autowired
    private SiteRepository siteRepository;

//    @Autowired
//    private SubsidiaryService subsidiaryService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;

    @PostMapping
    @ResponseBody
    public SiteWsDto getAllSites(@RequestBody SiteWsDto siteWsDto) {
        Pageable pageable = getPageable(siteWsDto.getPage(), siteWsDto.getSizePerPage(), siteWsDto.getSortDirection(), siteWsDto.getSortField());
        SiteDto siteDto = CollectionUtils.isNotEmpty(siteWsDto.getSites()) ? siteWsDto.getSites().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(siteDto, siteWsDto.getOperator());
        Site site = siteDto != null ? modelMapper.map(siteDto, Site.class) : null;
        Page<Site> page = isSearchActive(site) != null ? siteRepository.findAll(Example.of(site, exampleMatcher), pageable) : siteRepository.findAll(pageable);
        Type listType = new TypeToken<List<SiteDto>>() {
        }.getType();
        siteWsDto.setSites(modelMapper.map(page.getContent(), listType));
        siteWsDto.setBaseUrl(ADMIN_SITE);
        siteWsDto.setTotalPages(page.getTotalPages());
        siteWsDto.setTotalRecords(page.getTotalElements());
        siteWsDto.setAttributeList(getConfiguredAttributes(siteWsDto.getNode()));
        siteWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.SITE));
        return siteWsDto;
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.SITE);
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody SiteWsDto siteWsDto) {
        return getConfiguredAttributes(siteWsDto.getNode());
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
        Type listType = new TypeToken<List<SiteDto>>() {
        }.getType();
        siteWsDto.setSites(modelMapper.map(siteRepository.findByStatusOrderByIdentifier(true), listType));
        siteWsDto.setBaseUrl(ADMIN_SITE);
        return siteWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody SiteDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(siteRepository.findByIdentifier(recordId), SiteDto.class);
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
            siteRepository.deleteByIdentifier(siteDto.getIdentifier());
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
            sites.add(siteRepository.findByIdentifier(siteDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<SiteDto>>() {
        }.getType();
        siteWsDto.setSites(modelMapper.map(sites, listType));
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

    @PostMapping("/export")
    @ResponseBody
    public SiteWsDto uploadFile(@RequestBody SiteWsDto siteWsDto) {

        try {
            siteWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SITE, siteWsDto.getHeaderFields()));
            return siteWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
