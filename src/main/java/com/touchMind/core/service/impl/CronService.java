package com.touchMind.core.service.impl;

import java.util.Map;

public interface CronService {
    void processData(Map<String, String> data);

    void stopCronJob(String recordId);
}
