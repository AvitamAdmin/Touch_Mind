package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.SiteWsDto;
import com.touchMind.core.mongo.model.Site;

import java.util.List;

public interface SiteService {
    List<Site> findByStatusOrderBySiteId(Boolean status);

   // List<Site> findBySubsidiaryId(Subsidiary subsidiary);

    List<Site> findBySubsidiaryAndStatusOrderBySiteId(Boolean status);

    SiteWsDto handleEdit(SiteWsDto request);


}
