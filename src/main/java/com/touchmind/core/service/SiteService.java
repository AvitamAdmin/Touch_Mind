package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.SiteWsDto;
import com.touchmind.core.mongo.model.Site;
import com.touchmind.core.mongo.model.Subsidiary;

import java.util.List;

public interface SiteService {
    List<Site> findByStatusOrderBySiteId(Boolean status);

    List<Site> findBySubsidiaryId(Subsidiary subsidiary);

    List<Site> findBySubsidiaryAndStatusOrderBySiteId(Boolean status);

    SiteWsDto handleEdit(SiteWsDto request);


}
