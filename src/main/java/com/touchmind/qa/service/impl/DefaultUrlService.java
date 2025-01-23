package com.touchmind.qa.service.impl;

import com.touchmind.core.SpringContext;
import com.touchmind.core.mongo.model.Site;
import com.touchmind.core.mongo.repository.SiteRepository;
import com.touchmind.qa.service.UrlService;
import com.touchmind.qa.strategies.SubsidiaryType;
import com.touchmind.qa.utils.TestDataUtils;
import org.apache.commons.text.StringSubstitutor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@Service(SubsidiaryType.DEFAULT)
public class DefaultUrlService implements UrlService {

    public static final String DEFAULT_URL_SERVICE_TYPE = "DEFAULT";
    private static final Map<String, String> subsidiaryUrlServiceMappings = new HashMap<>();
    @Autowired
    private SiteRepository siteRepository;

    @Override
    public String getUrl(ITestContext context, String sku, String currentUrl) throws MalformedURLException {
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        Map<String, String> params = new HashMap<>();
        Site site = siteRepository.findByRecordId(TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE));
        if (site != null) {
            String siteCode = site.getIdentifier();
            params.put("temSite", siteCode);
            testData.put(TestDataUtils.Field.SITE_ISOCODE.toString(), siteCode);
            context.getSuite().setAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString(), testData);

        }
        params.put("temVariant", sku);
        //processExternalProductUrlForSku(params, sku);
        return processUrl(currentUrl, params);
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
}
