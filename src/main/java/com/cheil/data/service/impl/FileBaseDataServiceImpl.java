package com.cheil.data.service.impl;

import com.cheil.core.mongo.dto.ReportDto;
import com.cheil.data.BaseDataService;
import com.cheil.data.service.DataService;
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
