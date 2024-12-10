package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.CampaignDto;
import com.touchmind.core.mongo.dto.CampaignWsDto;
import com.touchmind.core.mongo.model.Campaign;
import com.touchmind.core.mongo.repository.CampaignRepository;
import com.touchmind.core.service.CampaignService;
import com.touchmind.core.service.CoreService;
import io.micrometer.common.util.StringUtils;
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

    public CampaignWsDto handleEdit(@RequestBody CampaignWsDto campaignWsDto) {
        List<CampaignDto> updatedCampaignList = new ArrayList<>();
        for (CampaignDto campaignDto : campaignWsDto.getCampaignList()) {
            Campaign campaign;
            if (StringUtils.isNotEmpty(campaignDto.getRecordId())) {
                campaign = campaignRepository.findByRecordId(campaignDto.getRecordId());
                if (campaign != null) {
                    modelMapper.map(campaignDto, campaign);
                    campaign.setLastModified(new Date());
                } else {
                    campaign = modelMapper.map(campaignDto, Campaign.class);
                    campaign.setCreationTime(new Date());
                    campaign.setCreator(coreService.getCurrentUser().getUsername());
                }
            } else {
                campaign = modelMapper.map(campaignDto, Campaign.class);
                campaign.setCreationTime(new Date());
                campaign.setCreator(coreService.getCurrentUser().getUsername());
            }
            campaign = campaignRepository.save(campaign);
            if (StringUtils.isEmpty(campaignDto.getRecordId())) {
                campaign.setRecordId(String.valueOf(campaign.getId().getTimestamp()));
                campaignRepository.save(campaign);
            }
            CampaignDto updatedCampaignDto = modelMapper.map(campaign, CampaignDto.class);
            updatedCampaignList.add(updatedCampaignDto);
        }
        campaignWsDto.setCampaignList(updatedCampaignList);
        return campaignWsDto;
    }

}
