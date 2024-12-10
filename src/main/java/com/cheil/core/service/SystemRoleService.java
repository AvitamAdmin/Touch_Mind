package com.cheil.core.service;

import com.cheil.core.mongo.dto.SystemRoleWsDto;

public interface SystemRoleService {
    SystemRoleWsDto handleEdit(SystemRoleWsDto request);
}
