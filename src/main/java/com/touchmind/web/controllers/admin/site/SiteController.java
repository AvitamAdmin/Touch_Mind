package com.touchmind.web.controllers.admin.site;


import com.touchmind.core.mongo.dto.SiteDto;
import com.touchmind.core.mongo.dto.SiteWsDto;
import com.touchmind.core.mongo.model.Site;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SiteRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.SiteService;
import com.touchmind.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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
    private SiteService siteService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

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
        //siteWsDto.setAttributeList(getConfiguredAttributes(siteWsDto.getNode()));
        //siteWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.SITE));
        return siteWsDto;
    }

//    @PostMapping("/saveSearchQuery")
//    @ResponseBody
//    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
//        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.SITE);
//    }

//    @GetMapping("/getAdvancedSearch")
//    @ResponseBody
//    public List<SearchDto> getSearchAttributes() {
//        return getGroupedParentAndChildAttributes(new Site());
//    }

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


//    @PostMapping("/upload")
//    @ResponseBody
//    public SiteWsDto uploadFile(@RequestParam("file") MultipartFile file) {
//        SiteWsDto siteWsDto = new SiteWsDto();
//        try {
//            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.SITE, EntityConstants.SITE, siteWsDto);
//            if (StringUtils.isEmpty(siteWsDto.getMessage())) {
//                siteWsDto.setMessage("File uploaded successfully!!");
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        return siteWsDto;
//    }
//
//    @GetMapping("/export")
//    @ResponseBody
//    public SiteWsDto uploadFile() {
//        SiteWsDto siteWsDto = new SiteWsDto();
//        try {
//            siteWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SITE));
//            return siteWsDto;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            return null;
//        }
//    }
}
