package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.SubsidiaryWsDto;
import com.touchmind.core.mongo.model.Subsidiary;

import java.util.List;

public interface SubsidiaryService {
    List<Subsidiary> findByStatusAndUserOrderByIdentifier(Boolean status);

    SubsidiaryWsDto handleEdit(SubsidiaryWsDto request);
}
