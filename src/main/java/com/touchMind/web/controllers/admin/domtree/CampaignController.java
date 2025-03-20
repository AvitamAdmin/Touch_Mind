package com.touchMind.web.controllers.admin.domtree;

import com.touchMind.core.mongo.dto.CampaignDto;
import com.touchMind.core.mongo.dto.CampaignWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Campaign;
import com.touchMind.core.mongo.repository.CampaignRepository;
import com.touchMind.core.mongo.repository.CrawlerPathRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.ShopNavigationRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CampaignService;
import com.touchMind.core.service.CoreService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.web.controllers.BaseController;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/campaigns")
public class CampaignController extends BaseController {

    public static final String ADMIN_CAMPAIGN = "/admin/campaigns";
    Logger logger = LoggerFactory.getLogger(CampaignController.class);
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private CrawlerPathRepository crawlerPathRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private ShopNavigationRepository shopNavigationRepository;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private FileExportService fileExportService;

    @PostMapping
    @ResponseBody
    public CampaignWsDto getAllCampaigns(@RequestBody CampaignWsDto campaignWsDto) {
        Pageable pageable = getPageable(campaignWsDto.getPage(), campaignWsDto.getSizePerPage(), campaignWsDto.getSortDirection(), campaignWsDto.getSortField());
        CampaignDto campaignDto = CollectionUtils.isNotEmpty(campaignWsDto.getCampaignDtoList()) ? campaignWsDto.getCampaignDtoList().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(campaignDto, campaignWsDto.getOperator());
        Campaign campaign = campaignDto != null ? modelMapper.map(campaignDto, Campaign.class) : null;
        Page<Campaign> page = isSearchActive(campaign) != null ? campaignRepository.findAll(Example.of(campaign, exampleMatcher), pageable) : campaignRepository.findAll(pageable);
        Type listType = new TypeToken<List<CampaignDto>>() {
        }.getType();
        campaignWsDto.setCampaignDtoList(modelMapper.map(page.getContent(), listType));
        campaignWsDto.setBaseUrl(ADMIN_CAMPAIGN);
        campaignWsDto.setTotalPages(page.getTotalPages());
        campaignWsDto.setTotalRecords(page.getTotalElements());
        campaignWsDto.setAttributeList(getConfiguredAttributes(campaignWsDto.getNode()));
        return campaignWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody CampaignWsDto campaignWsDto) {
        return getConfiguredAttributes(campaignWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Campaign());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.CAMPAIGN);
    }

    @GetMapping("/get")
    @ResponseBody
    public CampaignWsDto getActiveCampaign() {
        CampaignWsDto campaignWsDto = new CampaignWsDto();
        campaignWsDto.setBaseUrl(ADMIN_CAMPAIGN);
        Type listType = new TypeToken<List<CampaignDto>>() {
        }.getType();
        campaignWsDto.setCampaignDtoList(modelMapper.map(campaignRepository.findByStatusOrderByIdentifier(true), listType));
        return campaignWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody CampaignDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(campaignRepository.findByIdentifier(recordId), CampaignDto.class);
    }

    @GetMapping("/add")
    @ResponseBody
    public CampaignWsDto addCampaign() {
        CampaignWsDto campaignWsDto = new CampaignWsDto();
        campaignWsDto.setBaseUrl(ADMIN_CAMPAIGN);
        Type listType = new TypeToken<List<CampaignDto>>() {
        }.getType();
        campaignWsDto.setCampaignDtoList(modelMapper.map(campaignRepository.findByStatusOrderByIdentifier(true), listType));
        return campaignWsDto;
    }


    @PostMapping("/getedit")
    @ResponseBody
    public CampaignWsDto editCampaign(@RequestBody CampaignWsDto request) {
        List<Campaign> campaignList = new ArrayList<>();
        for (CampaignDto campaignDto : request.getCampaignDtoList()) {
            campaignList.add(campaignRepository.findByIdentifier(campaignDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<CampaignDto>>() {
        }.getType();
        request.setCampaignDtoList(modelMapper.map(campaignList, listType));
        request.setBaseUrl(ADMIN_CAMPAIGN);
        return request;
    }

    @PostMapping("/edit")
    @ResponseBody
    public CampaignWsDto handleEdit(@RequestBody CampaignWsDto campaignWsDto) {
        return campaignService.handleEdit(campaignWsDto);
    }


    @PostMapping("/delete")
    @ResponseBody
    public CampaignWsDto deleteCampaign(@RequestBody CampaignWsDto campaignWsDto) {
        for (CampaignDto campaignDto : campaignWsDto.getCampaignDtoList()) {
            campaignRepository.deleteByIdentifier(campaignDto.getIdentifier());
        }
        campaignWsDto.setMessage("Data deleted successfully");
        campaignWsDto.setBaseUrl(ADMIN_CAMPAIGN);
        return campaignWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public CampaignWsDto uploadFile(@RequestBody CampaignWsDto campaignWsDto) {
        try {
            campaignWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.CAMPAIGN, campaignWsDto.getHeaderFields()));
            return campaignWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
