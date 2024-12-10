package com.cheil.core.service;

import com.cheil.core.mongo.dto.SubsidiaryWsDto;
import com.cheil.core.mongo.model.Subsidiary;

import java.util.List;

public interface SubsidiaryService {
    List<Subsidiary> findByStatusAndUserOrderByIdentifier(Boolean status);

    SubsidiaryWsDto handleEdit(SubsidiaryWsDto request);
}
