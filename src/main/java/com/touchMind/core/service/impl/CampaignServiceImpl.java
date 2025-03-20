package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.CampaignDto;
import com.touchMind.core.mongo.dto.CampaignWsDto;
import com.touchMind.core.mongo.model.Campaign;
import com.touchMind.core.mongo.repository.CampaignRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CampaignService;
import com.touchMind.core.service.CoreService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;

    public CampaignWsDto handleEdit(@RequestBody CampaignWsDto campaignWsDto) {
        List<CampaignDto> updatedCampaignList = new ArrayList<>();
        for (CampaignDto campaignDto : campaignWsDto.getCampaignDtoList()) {
            if (campaignDto.isAdd() && baseService.validateIdentifier(EntityConstants.CAMPAIGN, campaignDto.getIdentifier()) != null) {
                campaignWsDto.setSuccess(false);
                campaignWsDto.setMessage("Identifier already present");
                return campaignWsDto;
            }
            Campaign campaign = campaignRepository.findByIdentifier(campaignDto.getIdentifier());
            if (campaign != null) {
                modelMapper.map(campaignDto, campaign);
                campaign.setLastModified(new Date());
            } else {
                campaign = modelMapper.map(campaignDto, Campaign.class);
                campaign.setCreationTime(new Date());
                campaign.setCreator(coreService.getCurrentUser().getUsername());
            }
            baseService.populateCommonData(campaign);
            campaign = campaignRepository.save(campaign);
            CampaignDto updatedCampaignDto = modelMapper.map(campaign, CampaignDto.class);
            updatedCampaignList.add(updatedCampaignDto);
        }
        campaignWsDto.setCampaignDtoList(updatedCampaignList);
        campaignWsDto.setMessage("Campaign updated successfully");
        return campaignWsDto;
    }

}
