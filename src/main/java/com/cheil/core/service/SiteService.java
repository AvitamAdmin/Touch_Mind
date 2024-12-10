package com.cheil.core.service;

import com.cheil.core.mongo.dto.SiteWsDto;
import com.cheil.core.mongo.model.Site;
import com.cheil.core.mongo.model.Subsidiary;

import java.util.List;

public interface SiteService {
    List<Site> findByStatusOrderBySiteId(Boolean status);

    List<Site> findBySubsidiaryId(Subsidiary subsidiary);

    List<Site> findBySubsidiaryAndStatusOrderBySiteId(Boolean status);

    SiteWsDto handleEdit(SiteWsDto request);


}
