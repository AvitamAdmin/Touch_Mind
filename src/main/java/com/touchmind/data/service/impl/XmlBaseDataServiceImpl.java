package com.touchmind.data.service.impl;

import com.touchmind.core.mongo.dto.ReportDto;
import com.touchmind.data.BaseDataService;
import com.touchmind.data.service.DataService;
import org.springframework.stereotype.Component;

@Component
public class XmlBaseDataServiceImpl extends BaseDataService implements DataService {

    @Override
    public String getType() {
        return null;
    }

    @Override
    public boolean processApi(ReportDto reportDto, String api) {
        return false;
    }
}
