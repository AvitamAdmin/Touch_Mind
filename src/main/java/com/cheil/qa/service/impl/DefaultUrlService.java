package com.cheil.qa.service.impl;

import com.cheil.core.SpringContext;
import com.cheil.core.mongo.model.Subsidiary;
import com.cheil.core.mongo.model.Variant;
import com.cheil.core.mongo.repository.SubsidiaryRepository;
import com.cheil.core.mongo.repository.VariantRepository;
import com.cheil.qa.service.UrlService;
import com.cheil.qa.strategies.SubsidiaryType;
import com.cheil.qa.utils.TestDataUtils;
import org.apache.commons.text.StringSubstitutor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(SubsidiaryType.DEFAULT)
public class DefaultUrlService implements UrlService {

    public static final String DEFAULT_URL_SERVICE_TYPE = "DEFAULT";
    private static final Map<String, String> subsidiaryUrlServiceMappings = new HashMap<>();
    @Autowired
    private SubsidiaryRepository subsidiaryRepository;

    public DefaultUrlService() {
        initSubsidiaryUrlServiceMapping();
    }

    @Override
    public String getUrl(ITestContext context, String sku, String currentUrl) throws MalformedURLException {
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        Map<String, String> params = new HashMap<>();
        params.put("temSite", TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE));
        //TODO check if the record is correctly fetched
        Subsidiary subsidiary = subsidiaryRepository.findByRecordId(testData.getString(TestDataUtils.Field.SUBSIDIARY.toString()));
        if (subsidiary != null) {
            params.put("temLocale", subsidiary.getIsoCode());
        }
        params.put("temVariant", sku);
        processExternalProductUrlForSku(params, sku);
        return processUrl(currentUrl, params);
    }

    public void processExternalProductUrlForSku(Map<String, String> params, String sku) {
        Variant variant = SpringContext.getBean(VariantRepository.class).findByIdentifier(sku);
        if (variant != null) {
            params.put("temExternalProductUrl", variant.getExternalProductUrl());
        }
    }

    public String processUrl(String rawUrl, Map<String, String> params) {
        return StringSubstitutor.replace(rawUrl, params, "$[", "]");
    }

    @Override
    public String getUrlServiceType(String subsidiary) {
        if (subsidiaryUrlServiceMappings.containsKey(subsidiary)) {
            return subsidiaryUrlServiceMappings.get(subsidiary);
        }
        return DEFAULT_URL_SERVICE_TYPE;
    }

    private void initSubsidiaryUrlServiceMapping() {
        SubsidiaryRepository subsidiaryRepository = SpringContext.getBean(SubsidiaryRepository.class);
        List<Subsidiary> subsidiaries = subsidiaryRepository.findByStatusOrderByIdentifier(true);
        subsidiaries.stream().forEach(subsidiary -> {
            subsidiaryUrlServiceMappings.put(subsidiary.getIdentifier(), DEFAULT_URL_SERVICE_TYPE);
        });
    }
}
