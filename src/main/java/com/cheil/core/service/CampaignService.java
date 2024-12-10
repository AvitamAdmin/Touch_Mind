package com.cheil.core.service;

import com.cheil.core.mongo.dto.CampaignWsDto;

public interface CampaignService {
    CampaignWsDto handleEdit(CampaignWsDto request);

}
