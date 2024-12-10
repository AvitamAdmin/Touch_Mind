package com.touchmind.qa.utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestDataUtils {


    public static final String COMMA = ",";
    public static final String PRD = "prd";
    public static final String STG = "stg";
    public static final String STG2 = "stg2";
    public static final String STG3 = "stg3";
    public static final String NUMBER_FORMAT_EXCEPTION_LOG_FORMAT = "NumberFormatException occurred when parsing the value \"%s\", retrieved from testData with the key \"%s\". Error message: %s";
    public static final String INVALID_URL = "Invalid URL";
    public static final String LITERAL_PERIOD_REGEX = "\\.";
    public static final String AEM_STAGED_ENVIRONMENT_PREFIX = "p6-pre-";
    public static final String ERROR_PARSING_URL = "Error parsing URL";
    static Logger LOG = LoggerFactory.getLogger(TestDataUtils.class);

    /**
     * Retrieves a string value from a map based on the specified key. If the key corresponds to a collection,
     * an optional index can be provided to retrieve a specific element from the collection.
     * <p>
     * Parameters:
     * - testData: Map<String, ?> - The source map containing the data.
     * - key: Field - The key corresponding to the value to be retrieved. It is an enum value representing the field name.
     * - id: int... (optional) - An optional index to specify which element to retrieve from a collection. If not provided,
     * or if the index is out of bounds, the first element of the collection is returned.
     * This parameter is ignored if the value is a single string.
     * <p>
     * Returns:
     * - String: The retrieved value. If the key corresponds to a collection and a valid index is provided,
     * the method returns the element at that index. If the key corresponds to a single string or
     * the index is not provided/out of bounds, it returns the string directly or the first element
     * of the collection, respectively. Returns an empty string if the key is not found or the collection is empty.
     */
    public static String getString(Map<String, ?> testData, Field key, int... id) {
        Object value = testData.get(key.toString());
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                return StringUtils.EMPTY;
            }
            if (id.length > 0 && id[0] >= 0) {
                // If the collection supports random access, use get method directly
                if (collection instanceof List<?> list) {
                    return id[0] < list.size() ? (String) list.get(id[0]) : StringUtils.EMPTY;
                } else {
                    // For other types of collections, iterate to the specified index
                    int currentIndex = 0;
                    for (Object obj : collection) {
                        if (currentIndex == id[0]) {
                            return (String) obj;
                        }
                        currentIndex++;
                    }
                }
            }
            // Default to the first element if id is not provided or out of bounds
            return (String) collection.iterator().next();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Retrieves a string value from a JSONObject based on a given key.
     * If the value is a comma-separated string, it returns the element at the specified index.
     * If the value is not comma-separated, it returns the entire string.
     * If the value is not a string or the index is out of bounds, it returns an empty string.
     *
     * @param testData The JSONObject to retrieve the value from.
     * @param key      The key whose value is to be retrieved.
     * @param id       Optional parameter to specify the index of the value in a comma-separated string.
     * @return The retrieved string value, a specific element from a comma-separated string, or an empty string.
     */
    public static String getString(JSONObject testData, Field key, int... id) {
        try {
            String value = testData.getString(key.toString());

            // Check if the value is comma-separated and id is provided
            if (value.contains(",") && id.length > 0) {
                String[] parts = value.split(",");
                // Check if the provided index is within the range
                if (id[0] >= 0 && id[0] < parts.length) {
                    return parts[id[0]];
                }
            } else {
                // Return the full value if it's not comma-separated
                return value;
            }
        } catch (JSONException e) {
            LOG.info(e + e.getMessage());
        }
        // Return an empty string if the value is not a string or the index is out of bounds
        return "";
    }

    public static Map<String, Object> getMap(JSONObject testData, Field key) {
        try {
            JSONObject obj = testData.getJSONObject(key.toString());
            if (Objects.nonNull(obj)) {
                return toMap(obj);
            }
        } catch (Exception e) {
            LOG.info(e + e.getMessage());
            return null;
        }
        return null;
    }

    public static Map<String, Object> toMap(JSONObject obj) {
        Map<String, Object> results = new HashMap<>();
        for (String entry : obj.keySet()) {
            Object entryValue = obj.get(entry);
            Object value;
            if (entryValue == null || "null".equals(entryValue)) {
                value = null;
            } else if (entryValue instanceof JSONObject) {
                value = ((JSONObject) entryValue).toMap();
            } else if (entryValue instanceof JSONArray) {
                value = new HashSet<>(((JSONArray) entryValue).toList());
            } else {
                value = entryValue;
            }
            results.put(entry, value);
        }
        return results;
    }

    public static int getInt(Map<String, ?> testData, Field key, int... id) {
        String stringValue = getString(testData, key, id);
        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            LOG.error(String.format(NUMBER_FORMAT_EXCEPTION_LOG_FORMAT,
                    stringValue,
                    key,
                    e.getMessage()));

            return -1;
        }
    }

    /**
     * Extracts environment code from Hybris/AEM environments. Sample input-output:
     * https://p6-pre-qa2.samsung.com/aemapi/v6 -> "p6-pre-qa2"
     * https://p6-pre-qa3.samsung.com/aemapi/v6 -> "p6-pre-qa3"
     * https://samsung.com/aema -> "prd"
     * https://stg2.shop.samsung.com/getcookie.html -> "stg2"
     * https://shop.samsung.com/getcookie.html -> "prd"
     *
     * @param url the URL string
     * @return the extracted part of the URL
     */
    private static String extractEnvironmentCodeFromUrl(String url) {
        // Check if the URL is null or empty
        if (url == null || url.isEmpty()) {
            return INVALID_URL;
        }

        // Try to parse the URL and extract the host
        try {
            java.net.URL parsedUrl = new java.net.URL(url);
            String host = parsedUrl.getHost();

            // Split the host by dots
            String[] parts = host.split(LITERAL_PERIOD_REGEX);

            String subdomain = parts[0];
            if (subdomain.startsWith(AEM_STAGED_ENVIRONMENT_PREFIX) || subdomain.startsWith(STG)) {
                return subdomain;
            } else {
                return PRD;
            }

        } catch (Exception e) {
            return ERROR_PARSING_URL;
        }
    }

    public enum Field {
        SKUS("skus"),
        SKU("sku"),
        NODE("node"),
        TEST_COUNT("testCount"),
        REPORT_FILE_NAME("reportFileName"),
        TESTNG_CONTEXT_PARAM_NAME("data"),
        ATOMIC_COUNTER("atomicCounter"),
        EXTENT_MANAGER("extentManager"),
        TEST_PLAN("testPlan"),
        ERROR_DATA("errorData"),
        ERROR_MSG("errorMsg"),
        NOTIFICATION_DATA("notificationData"),
        SITE_ISOCODE("siteIsoCode"),
        TEST_NAME("testName"),
        THREAD_CONTEXT("threadContext"),
        ORDER_NUMBER("orderNumber"),
        TEST_PASSED_COUNT("testPassedCount"),
        SESSION_ID("sessionId"),
        SUBSIDIARY("subsidiary"),
        ENVIRONMENT("environment"),
        GREATER_THAN("Greater or equals"),
        LESS_THAN("Less than or equals"),
        EQUALS("Equals"),
        NOT_EMPTY("Not empty"),
        TOOLKIT_SESSIONS("toolkitSessions"),
        TITLE("title"),
        EMAILS("emails"),
        JOB_TIME("jobTime"),
        REPORT_URL("reportUrl"),
        SERVER_URL("serverUrl"),
        NOT_NULL("Not null"),
        PAYMENT_TYPE("paymentType"),
        DELIVERY_TYPE("deliveryType"),
        TEST_PROFILE("testProfile"),
        JOB_TYPE("cronJob"),
        EMAIL_SUBJECT("emailSubject"),
        DASHBOARD("dashboard"),
        CRON_CURRENT_USER("identifier"),
        IS_DEBUG("isDebug"), CRON_PROFILE_ID("cronProfileId"),
        SHOP_CAMPAIGN("shopCampaign");
        private final String field;

        Field(String field) {
            this.field = field;
        }

        public String toString() {
            return field;
        }
    }
}
