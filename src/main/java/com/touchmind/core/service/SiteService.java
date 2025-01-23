package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.SiteWsDto;
import com.touchmind.core.mongo.model.Site;

import java.util.List;

public interface SiteService {
    List<Site> findByStatusOrderBySiteId(Boolean status);

    SiteWsDto handleEdit(SiteWsDto request);


}
