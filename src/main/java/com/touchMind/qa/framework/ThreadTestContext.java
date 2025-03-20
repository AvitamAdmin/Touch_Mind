package com.touchMind.qa.framework;

import com.aventstack.extentreports.ExtentTest;
import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
@Scope("prototype")
public class ThreadTestContext {
    public WebDriver driver;
    public FluentWait<WebDriver> fluentWait;
    public ExtentTest extentTest;
    public int testIdentifier; //allows each test to identify SKUs or other test-specific data amongst other tests running in parallel
    public Map<String, String> data = new HashMap<>(); //for storing variable data
    private Instant lastLogTime = Instant.now();
}
