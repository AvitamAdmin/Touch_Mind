package com.touchmind.core.service;

import java.util.Map;

public interface CommonService {
    Map<String, String> toMap(Object object);

    Map<String, String> convertToMap(Object object);
}
