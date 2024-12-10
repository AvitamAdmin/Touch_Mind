package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.CampaignWsDto;

public interface CampaignService {
    CampaignWsDto handleEdit(CampaignWsDto request);

}
