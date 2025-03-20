package com.touchMind.data;

import com.touchMind.data.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataServiceFactory {
    @Autowired
    private List<DataService> services;

    public DataService getService(final String type) {
        return services
                .stream().filter(service -> type.equals(service.getType()))
                .findFirst()
                .orElseThrow((() -> new RuntimeException("No data process found " + type)));
    }
}
