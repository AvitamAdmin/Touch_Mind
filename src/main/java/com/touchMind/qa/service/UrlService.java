package com.touchMind.qa.service;

import org.testng.ITestContext;

import java.net.MalformedURLException;

public interface UrlService {
    String getUrl(ITestContext context, String sku, String currentUrl) throws MalformedURLException;

    String getUrlServiceType(String subsidiary);
}
