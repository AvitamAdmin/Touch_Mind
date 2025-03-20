package com.touchMind;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.function.Function;


public class ZeroInMultipleActionsTest {
    public static final String JAVASCRIPT_SCROLL_INTO_VIEW = "arguments[0].scrollIntoView();";

    public static void main(String[] args) throws InterruptedException {
        browseOmniElements();
        //NarrayTreeNode node = new NarrayTreeNode("root","root");
        //node.constructTree();
        //testPath();
    }

    private static void browseOmniElements() throws InterruptedException {
        ChromeDriver driver = new ChromeDriver();
        driver.get("https://www.samsung.com/de/offer/the-voice-of-germany/?category=tvAudio");
        //driver.get("https://www.samsung.com/de/offer/the-voice-of-germany/?category=topdeals");
        final By by = By.id("truste-consent-button");
        //Thread.sleep(6000);


        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(50))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class);
        WebElement clickseleniumlink = wait.until(new Function<WebDriver, WebElement>() {

            public WebElement apply(WebDriver driver) {
                return driver.findElement(by);
            }
        });

        clickseleniumlink.click();
        final By by1 = By.cssSelector("[data-omni='de:promotion:;GQ85QN900DT;10999|GQ85QN900DTXZG|null:Top Deals:null:product-component:add_to_basket']");
        clickseleniumlink = wait.until(driver1 -> driver1.findElement(by1));
        driver.executeScript(JAVASCRIPT_SCROLL_INTO_VIEW, clickseleniumlink);
        clickseleniumlink.click();

        /*Set<String> handles = driver.getWindowHandles();
        Iterator<String> it = handles.iterator();
        it.next();
        driver.switchTo().window(it.next());
        driver.close();
        By by2 = By.cssSelector("[data-omni='de:promotion:go to_/de/smartphones/galaxy-s24/buy/|null:Galaxy S-Serie:null:teaser-component:learn_more']");
        WebElement element = null; element = driver.findElement(by1);
        element.click();
        Thread.sleep(10000);*/

        By by2 = By.cssSelector("[data-omni='de:promotion:go to cart_Zum Warenkorb']");
        WebElement element = driver.findElement(by2);
        element.click();
        System.out.println(element);
        driver.navigate().back();
        System.out.println(element);
        /*
        List<WebElement> pageElements = driver.findElements(by);
        pageElements.stream().forEach(e ->{
            System.out.println(e.getAttribute("data-omni"));
        });

         */
        driver.close();
    }

    private static String testPath() {
        String path = "de:promotion:go to pdp_/de/tvs/uhd-4k-tv/du8079-43-inch-crystal-uhd-4k-tizen-os-smart-tv-gu43du8079uxzg/|null:Top Deals:null:product-component:image_click";
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        String[] attributes = path.split(":");
        if (attributes.length < 2) {
            return null;
        }
        String key = attributes[attributes.length - 2] + ":" + attributes[attributes.length - 1];
        System.out.println(key);
        return key;
    }
}
