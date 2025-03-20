package com.touchMind.data.service.impl;

import com.touchMind.core.mongo.dto.ReportDto;
import com.touchMind.data.BaseDataService;
import com.touchMind.data.service.DataService;
import org.springframework.stereotype.Component;

@Component
public class FileBaseDataServiceImpl extends BaseDataService implements DataService {
    @Override
    public String getType() {
        return null;
    }

    @Override
    public boolean processApi(ReportDto reportDto, String api) {
        return false;
    }
}
