package com.cheil.core.service.impl;

import com.cheil.core.service.CommonService;
import com.google.gson.Gson;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CommonServiceImpl implements CommonService {

    Logger LOG = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Gson gson;

    @Override
    public Map<String, String> toMap(Object object) {
        Map<String, String> map = new LinkedHashMap<>();
        modelMapper.map(object, map);
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value instanceof String) {
                    map.put(field.getName(), ObjectUtils.isNotEmpty(value) ? value.toString() : "");
                } else {
                    String jsonObject = gson.toJson(value);
                    map.put(field.getName(), ObjectUtils.isNotEmpty(jsonObject) ? jsonObject : "");
                }
            }
            for (Field field : object.getClass().getSuperclass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value instanceof String) {
                    map.put(field.getName(), ObjectUtils.isNotEmpty(value) ? value.toString() : "");
                } else {
                    String jsonObject = gson.toJson(value);
                    map.put(field.getName(), ObjectUtils.isNotEmpty(jsonObject) ? jsonObject : "");
                }
            }
        } catch (Exception exp) {
            LOG.error(exp.getMessage());
        }
        return map;
    }

    public Map<String, String> convertToMap(Object object) {
        Map<String, String> map = new LinkedHashMap<>();
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(object);
                if (ObjectUtils.isNotEmpty(value)) {
                    if (value instanceof String) {
                        map.put(field.getName(), value.toString());
                        field.set(object, value.toString().split("\\|")[0]);
                    } else {
                        String jsonObject = gson.toJson(value);
                        if (ObjectUtils.isNotEmpty(jsonObject)) {
                            map.put(field.getName(), jsonObject);
                            field.set(object, jsonObject.split("\\|")[0]);
                        }
                    }
                }
            }
            for (Field field : object.getClass().getSuperclass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(object);
                if (ObjectUtils.isNotEmpty(value)) {
                    if (value instanceof String) {
                        map.put(field.getName(), value.toString());
                        field.set(object, value.toString().split("\\|")[0]);
                    } else {
                        String jsonObject = gson.toJson(value);
                        if (ObjectUtils.isNotEmpty(jsonObject)) {
                            map.put(field.getName(), jsonObject);
                            field.set(object, jsonObject.split("\\|")[0]);
                        }
                    }
                }
            }
            for (Field field : object.getClass().getSuperclass().getSuperclass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(object);
                if (ObjectUtils.isNotEmpty(value)) {
                    if (value instanceof String) {
                        map.put(field.getName(), value.toString());
                        field.set(object, value.toString().split("\\|")[0]);
                    } else {
                        String jsonObject = gson.toJson(value);
                        if (ObjectUtils.isNotEmpty(jsonObject)) {
                            map.put(field.getName(), jsonObject);
                            field.set(object, jsonObject.split("\\|")[0]);
                        }
                    }
                }
            }
        } catch (Exception exp) {
            LOG.error(exp.getMessage());
        }
        return map;
    }
}
