package com.cheil.qa.strategies;

import com.cheil.qa.service.UrlService;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;

import java.net.MalformedURLException;
import java.util.Map;

@Component
public class UrlFactory {

    private final Map<String, UrlService> urlServiceMap;

    public UrlFactory(Map<String, UrlService> urlServiceMap) {
        this.urlServiceMap = urlServiceMap;
    }

    public UrlService getUrlService(String subsidiaryType) {
        UrlService urlService = urlServiceMap.get(subsidiaryType);
        if (urlService == null) {
            throw new RuntimeException("Unsupported subsidiary type");
        }
        return urlService;
    }

    public String constructUrl(ITestContext context, String subsidiaryType, String sku, String currentUrl) throws MalformedURLException {
        UrlService urlService = getUrlService(subsidiaryType);
        return urlService.getUrl(context, sku, currentUrl);
    }
}
