package com.touchmind.qa.crawler.selector;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrawlerElementAttributes {
    private String locator;
    private String rootLocator;
    private String methodType;
    private String crawlerLocatorType;
    private boolean isTakeAScreenshot;
    private String waitTime;
    private String component;
}
