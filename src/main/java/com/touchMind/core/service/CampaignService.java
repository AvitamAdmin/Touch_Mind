package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.CampaignWsDto;

public interface CampaignService {
    CampaignWsDto handleEdit(CampaignWsDto request);

}
