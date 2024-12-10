package com.cheil.qa.utils;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.cheil.qa.framework.ExtentManager;
import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.utils.BeanUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;


public class ReportUtils {

    public static final String AES = "AES";
    public static final String ACTION_DESCRIPTION = "action.description";
    public static final String ACTION_IDENTIFIER = "action.identifier";
    public static final String ERROR_ACTION_FAILED = "error.action.failed";
    public static final String REPORT_ERROR_MESSAGE = "%s: \"%s\".";
    public static final String REPORT_ACTION_MESSAGE = "%s: \"%s\". %s: \"%s\".";
    public static final String ELEMENT_CODE = "element.code";
    public static final String HTML_BADGE_TEMPLATE = "<span class=\"badge badge-danger\" title=\"%s\">log</span>";
    public static final String ADDING_EXTENT_REPORTS_ENTRY_TO_TEST = "Adding ExtentReports entry to the test \"%s\":\n%s\n%s";
    public static final String SCREENSHOTS_DIR = "screenshots/";
    public static final String FILE_NAME_FORMAT = "%s_%d.jpg";

    public static final String FILE_NAME_FORMAT_HTML = "%s_%d.html";
    public static final String ERROR_SCREENSHOT_FAILED = "error.occurred.while.taking.a.screenshot";
    public static final String KEY_VALUE_FORMAT = "%s: %s";
    public static final String S_WAITING_TIME_F_SECONDS = "s.waiting.time.f.seconds";
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String KEY_ZEROINRYPTION = "KeyZEROINryption";
    public static final String AES_ECB_PKCS_5_PADDING = "AES/ECB/PKCS5Padding";
    public static final String ALGORITHM = "AES";
    private static final Logger LOG = LoggerFactory.getLogger(ReportUtils.class);
    //@Value("${extent.report.compression.type}")
    static String extentReportCompressionType = "Base64";
    //@Value("extent.report.compression.size}")
    static Integer extentReportCompressionSize = 1;

    public static Media info(ITestContext context, String details, String identifier, boolean takeScreenshot) {
        return doLog(context, Status.INFO, details, identifier, takeScreenshot);
    }

    private static Media error(ITestContext context, String details, String spanErrorMessage, String identifier, boolean takeScreenshot) {
        return doLog(context, Status.INFO, details + generateSpanLog(spanErrorMessage), identifier, takeScreenshot);
    }

    public static Media fail(ITestContext context, String details, String identifier, boolean takeScreenshot) {
        return doLog(context, Status.FAIL, details, identifier, takeScreenshot);
    }

    public static Media reportError(ITestContext context, String descriptionForReporting, String identifier, Throwable e, boolean takeScreenshot) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        return ReportUtils.error(context,
                String.format(REPORT_ERROR_MESSAGE,
                        BeanUtils.getLocalizedString(ERROR_ACTION_FAILED),
                        descriptionForReporting),
                e.getMessage(), identifier,
                takeScreenshot);
    }

    public static Media reportAction(ITestContext context, WebElement element, String actionDescriptionForReporting, String actionIdentifier, boolean takeAScreenshot) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        String localizedActionDescriptionForReporting = BeanUtils.getLocalizedString(actionDescriptionForReporting);
        String e = element != null ? element.toString() : null;
        return ReportUtils.info(context,
                String.format(REPORT_ACTION_MESSAGE,
                        BeanUtils.getLocalizedString(ACTION_DESCRIPTION),
                        localizedActionDescriptionForReporting,
                        BeanUtils.getLocalizedString(ELEMENT_CODE),
                        e), actionIdentifier,
                takeAScreenshot);
    }

    public static Media reportAction(ITestContext context, List<WebElement> elements, String actionDescriptionForReporting, String actionIdentifier, boolean takeAScreenshot) {
        String localizedActionDescriptionForReporting = BeanUtils.getLocalizedString(actionDescriptionForReporting);
        String e = elements != null ? elements.toString() : null;
        return ReportUtils.info(context,
                String.format(REPORT_ACTION_MESSAGE,
                        BeanUtils.getLocalizedString(ACTION_DESCRIPTION),
                        localizedActionDescriptionForReporting,
                        BeanUtils.getLocalizedString(ELEMENT_CODE),
                        "Found " + elements.size() + " elements > " + e), actionIdentifier,
                takeAScreenshot);
    }

    private static String generateSpanLog(String message) {
        if (StringUtils.isNotEmpty(message)) {
            return String.format(HTML_BADGE_TEMPLATE, StringEscapeUtils.escapeHtml4(message));
        } else {
            return StringUtils.EMPTY;
        }
    }

    private static Media doLog(ITestContext context, Status status, String details, String identifier, boolean takeScreenshot) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        LOG.debug(String.format(ADDING_EXTENT_REPORTS_ENTRY_TO_TEST,
                threadTestContext.getExtentTest().getModel().getName(),
                details, BeanUtils.getLocalizedString(ACTION_IDENTIFIER) + ": " + identifier));

        if (StringUtils.isNotEmpty(identifier)) {
            details = details + " " + BeanUtils.getLocalizedString(ACTION_IDENTIFIER) + ":" + identifier;
        }
        Instant now = Instant.now();
        double timeDiffInSeconds = Duration.between(threadTestContext.getLastLogTime(), now).toMillis() / 1000.0;
        threadTestContext.setLastLogTime(now); // Update the last log time
        if (timeDiffInSeconds > 1) {
            details = String.format(
                    BeanUtils.getLocalizedString(S_WAITING_TIME_F_SECONDS),
                    details,
                    timeDiffInSeconds);
        }

        Media screenshot = null;

        if (takeScreenshot) {
            screenshot = takePageScreenshot(context, identifier); //takePageScreenshot may return null
        }

       /*

        if (takeScreenshot) {
        if(extentReportCompressionType != null && "Base64".equals(extentReportCompressionType)) {
            screenshot = takePageScreenshotBase64(context, identifier, takeScreenshot); //takePageScreenshot may return null
        } else {
            screenshot = takePageScreenshot(context, identifier, takeScreenshot);
        }
    }
*/

        if (screenshot != null) {
            threadTestContext.getExtentTest().log(status, details, screenshot);
        } else {
            threadTestContext.getExtentTest().log(status, details);
        }
        return screenshot;
    }

    public static String savePageSource(ITestContext context, String serverUrl) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String fileName = TestDataUtils.getString(testData, TestDataUtils.Field.REPORT_FILE_NAME);

        try {
            String currentTimeInMills = String.valueOf(System.currentTimeMillis());
            //looking for the unique file name
            File file;
            String newFileName;
            int counter = 1;
            do {
                newFileName = String.format(FILE_NAME_FORMAT_HTML, fileName + "_pageSource" + "_" + currentTimeInMills, counter);
                file = new File(ExtentManager.REPORT_PATH + newFileName);
                counter++;
            } while (file.exists());
            Path path = Paths.get(ExtentManager.REPORT_PATH + newFileName);
            Files.writeString(path, threadTestContext.getDriver().getPageSource());
            return serverUrl + "/reports/" + file.getName();
        } catch (Exception e) {
            error(context,
                    String.format(KEY_VALUE_FORMAT,
                            "Error while saving page source",
                            e.getMessage()),
                    StringUtils.EMPTY, "",
                    true);
        }
        return null;
    }

    /**
     * takes a screenshot and returns its path
     *
     * @return media
     */
    private static Media takePageScreenshot(ITestContext context, String identifier) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String fileName = TestDataUtils.getString(testData, TestDataUtils.Field.REPORT_FILE_NAME);

        try {
            String currentTimeInMills = String.valueOf(System.currentTimeMillis());
            //looking for the unique file name
            File file;
            String newFileName;
            int counter = 1;
            do {
                newFileName = String.format(FILE_NAME_FORMAT, fileName + "_" + currentTimeInMills, counter);

                file = new File(ExtentManager.REPORT_PATH + SCREENSHOTS_DIR + newFileName);
                counter++;
            } while (file.exists());

            //taking a screenshot
            File src = ((TakesScreenshot) threadTestContext.getDriver()).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, file);
            String path = SCREENSHOTS_DIR + newFileName;
            return MediaEntityBuilder.createScreenCaptureFromPath(path).build();
        } catch (Exception e) {
            error(context,
                    String.format(KEY_VALUE_FORMAT,
                            BeanUtils.getLocalizedString(ERROR_SCREENSHOT_FAILED),
                            e.getMessage()),
                    StringUtils.EMPTY, identifier,
                    true);
        }
        return null;
    }

    public static String getMaskedString(String value, Boolean isMasked) {
        return BooleanUtils.isTrue(isMasked) ? getMaskedString(value) : value;
    }

    public static String getMaskedString(String value) {
        return StringUtils.isNotEmpty(value) ? encrypt(value) : StringUtils.EMPTY;
    }

    public static String encrypt(String stringToBeEncrypted) {
        Cipher cipher = getEncryptionService(Cipher.ENCRYPT_MODE, stringToBeEncrypted);
        //Encryption
        byte[] cipherText = new byte[0];
        try {
            cipherText = cipher.doFinal(stringToBeEncrypted.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        //Encode Characters
        return Base64.encodeBase64URLSafeString(cipherText);
    }

    public static String decrypt(String encodedTxt) {
        Cipher cipher = getEncryptionService(Cipher.DECRYPT_MODE, encodedTxt);
        String decodeStr = null;
        try {
            decodeStr = URLDecoder.decode(encodedTxt, StandardCharsets.UTF_8);
            //Decode - to base 64 Safe
            byte[] base64decodedTokenArr = Base64.decodeBase64(decodeStr.getBytes(StandardCharsets.UTF_8));
            byte[] decryptedPassword = new byte[0];
            decryptedPassword = cipher.doFinal(base64decodedTokenArr);
            return new String(decryptedPassword);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return "";
    }

    private static Cipher getEncryptionService(int mode, String value) {
        //This is the Key that we are using for encryption. We will use the same key for decryption
        byte[] keyBytes = KEY_ZEROINRYPTION.getBytes();
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(AES_ECB_PKCS_5_PADDING);
            SecretKeySpec key = new SecretKeySpec(keyBytes, ALGORITHM);
            cipher.init(mode, key);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return cipher;
    }

    public static void logMessage(ITestContext context, boolean isDebug, String message) {
        if (isDebug) {
            info(context, message, null, false);
        }
    }

    /**
     * takes a screenshot and returns its path
     *
     * @return media
     */
    private static Media takePageScreenshotBase64(ITestContext context, String identifier, boolean takeScreenshot) {
        if (!takeScreenshot) return null;
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String fileName = TestDataUtils.getString(testData, TestDataUtils.Field.REPORT_FILE_NAME);

        try {
            String currentTimeInMills = String.valueOf(System.currentTimeMillis());
            //looking for the unique file name
            File file;
            String newFileName;
            int counter = 1;
            do {
                newFileName = String.format(FILE_NAME_FORMAT, fileName + "_" + currentTimeInMills, counter);
                file = new File(ExtentManager.REPORT_PATH + SCREENSHOTS_DIR + newFileName);
                counter++;
            } while (file.exists());

            //taking a screenshot
            byte[] bytes = ((TakesScreenshot) threadTestContext.getDriver()).getScreenshotAs(OutputType.BYTES);
            InputStream is = new ByteArrayInputStream(bytes);
            BufferedImage src = ImageIO.read(is);
            /*
            if(extentReportCompressionSize != null && extentReportCompressionSize > 0) {
                src = Scalr.resize(src, Scalr.Method.SPEED, src.getHeight() / extentReportCompressionSize, src.getWidth() / extentReportCompressionSize);
            }

             */
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                ImageIO.write(src, "PNG", os);
                bytes = os.toByteArray();
            }
            String base64 = java.util.Base64.getEncoder().encodeToString(bytes);

            return MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build();
        } catch (Exception e) {
            error(context,
                    String.format(KEY_VALUE_FORMAT,
                            BeanUtils.getLocalizedString(ERROR_SCREENSHOT_FAILED),
                            e.getMessage()),
                    StringUtils.EMPTY, identifier,
                    takeScreenshot);
        }
        return null;
    }
}
