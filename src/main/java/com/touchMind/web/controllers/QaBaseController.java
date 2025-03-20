package com.touchMind.web.controllers;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class QaBaseController extends BaseController {
    protected FieldNameAndFieldValue isSearchActive(Map<String, String> allRequestParams) {
        FieldNameAndFieldValue fieldNameAndFieldValue = new FieldNameAndFieldValue();
        AtomicReference<String> isSearchable = new AtomicReference(null);
        allRequestParams.keySet().forEach(key -> {
            if (key.endsWith("[search][value]") && StringUtils.isNotEmpty(allRequestParams.get(key))) {
                String dataFieldKey = key.substring(0, key.length() - "[search][value]".length()) + "[data]";
                fieldNameAndFieldValue.setFieldName(allRequestParams.get(dataFieldKey));
                fieldNameAndFieldValue.setFieldValue(allRequestParams.get(key));
                isSearchable.set(allRequestParams.get(dataFieldKey));//columns[0][data]
            }
        });
        return fieldNameAndFieldValue;
    }
}
