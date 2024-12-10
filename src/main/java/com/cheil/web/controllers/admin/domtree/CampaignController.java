package com.cheil.web.controllers.admin.domtree;

import com.cheil.core.mongo.dto.CampaignDto;
import com.cheil.core.mongo.dto.CampaignWsDto;
import com.cheil.core.mongo.model.Campaign;
import com.cheil.core.mongo.repository.CampaignRepository;
import com.cheil.core.mongo.repository.CrawlerPathRepository;
import com.cheil.core.mongo.repository.ShopNavigationRepository;
import com.cheil.core.service.CampaignService;
import com.cheil.core.service.CoreService;
import com.cheil.web.controllers.BaseController;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    @ResponseBody
    public CampaignWsDto getAllCampaigns(@RequestBody CampaignWsDto campaignWsDto) {
        Pageable pageable = getPageable(campaignWsDto.getPage(), campaignWsDto.getSizePerPage(), campaignWsDto.getSortDirection(), campaignWsDto.getSortField());
        CampaignDto campaignDto = CollectionUtils.isNotEmpty(campaignWsDto.getCampaignList()) ? campaignWsDto.getCampaignList().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(campaignDto, campaignWsDto.getOperator());
        Campaign campaign = campaignDto != null ? modelMapper.map(campaignDto, Campaign.class) : null;
        Page<Campaign> page = isSearchActive(campaign) != null ? campaignRepository.findAll(Example.of(campaign, exampleMatcher), pageable) : campaignRepository.findAll(pageable);
        campaignWsDto.setCampaignList(modelMapper.map(page.getContent(), List.class));
        campaignWsDto.setBaseUrl(ADMIN_CAMPAIGN);
        campaignWsDto.setTotalPages(page.getTotalPages());
        campaignWsDto.setTotalRecords(page.getTotalElements());
        campaignWsDto.setAttributeList(getConfiguredAttributes(campaignWsDto.getNode()));
        return campaignWsDto;
    }

    @GetMapping("/get")
    @ResponseBody
    public CampaignWsDto getActiveCampaign() {
        CampaignWsDto campaignWsDto = new CampaignWsDto();
        campaignWsDto.setBaseUrl(ADMIN_CAMPAIGN);
        campaignWsDto.setCampaignList(modelMapper.map(campaignRepository.findByStatusOrderByIdentifier(true), List.class));
        return campaignWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public CampaignWsDto addCampaign() {
        CampaignWsDto campaignWsDto = new CampaignWsDto();
        campaignWsDto.setBaseUrl(ADMIN_CAMPAIGN);
        campaignWsDto.setCampaignList(modelMapper.map(campaignRepository.findByStatusOrderByIdentifier(true), List.class));
        return campaignWsDto;
    }


    @PostMapping("/getedit")
    @ResponseBody
    public CampaignWsDto editCampaign(@RequestBody CampaignWsDto campaignWsDto) {
        List<CampaignDto> updatedCampaignList = new ArrayList<>();
        for (CampaignDto campaignDto : campaignWsDto.getCampaignList()) {
            Campaign campaign = campaignRepository.findByRecordId(campaignDto.getRecordId());
            if (campaign != null) {
                CampaignDto mappedDto = modelMapper.map(campaign, CampaignDto.class);
                campaignRepository.save(campaign);
                updatedCampaignList.add(mappedDto);
            } else {
                updatedCampaignList.add(campaignDto);
            }
        }
        campaignWsDto.setCampaignList(updatedCampaignList);
        return campaignWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public CampaignWsDto handleEdit(@RequestBody CampaignWsDto campaignWsDto) {
        return campaignService.handleEdit(campaignWsDto);
    }

    @GetMapping("/delete")
    @ResponseBody
    public CampaignWsDto deleteCampaign(@RequestBody CampaignWsDto campaignWsDto) {
        for (CampaignDto campaignDto : campaignWsDto.getCampaignList()) {
            campaignRepository.deleteByRecordId(campaignDto.getRecordId());
        }
        return campaignWsDto;
    }
}
