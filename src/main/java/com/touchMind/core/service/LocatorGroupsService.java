package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.LocatorGroupWsDto;
import com.touchMind.core.mongo.model.LocatorGroup;

import java.util.List;

public interface LocatorGroupsService {

    List<LocatorGroup> findByStatusOrderByIdentifier(Boolean status);

    LocatorGroupWsDto handleEdit(LocatorGroupWsDto request);
}
