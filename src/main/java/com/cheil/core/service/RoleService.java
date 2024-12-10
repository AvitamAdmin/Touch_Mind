package com.cheil.core.service;

import com.cheil.core.mongo.dto.RoleWsDto;

public interface RoleService {

    RoleWsDto handelCopy(RoleWsDto request);

    RoleWsDto handleEdit(RoleWsDto request);
}
