package com.touchmind.qa.validation;

import com.touchmind.qa.utils.ReportUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service
public class ParamValidatorService {

    public static final String EQUALS = "Equals";
    public static final String GREATER_OR_EQUALS = "Greater or equals";
    public static final String LESS_THAN_OR_EQUALS = "Less than or equals";
    public static final String NOT_EMPTY = "Not empty";
    public static final String NOT_NULL = "Not null";

    public boolean validate(ITestContext context, String currentVariant, String paramValue, String condition, String apiValue) {
        switch (condition) {
            case EQUALS:
                if (StringUtils.isNotEmpty(paramValue) && StringUtils.isNotEmpty(apiValue) && !paramValue.equals(apiValue)) {
                    ReportUtils.fail(context, "Validation failed " + paramValue + " " + condition + " " + apiValue, StringUtils.EMPTY, false);
                    return false;
                }
                break;
            case GREATER_OR_EQUALS:
                if (NumberUtils.isCreatable(paramValue) && NumberUtils.isCreatable(paramValue)) {
                    Double pNumber = Double.parseDouble(paramValue);
                    Double apiNumber = Double.parseDouble(apiValue);
                    if (apiNumber < pNumber) {
                        ReportUtils.fail(context, "Validation failed " + paramValue + " " + condition + " " + apiValue, StringUtils.EMPTY, false);
                        return false;
                    }
                } else {
                    ReportUtils.fail(context, "Validation failed " + paramValue + " " + condition + " " + apiValue, StringUtils.EMPTY, false);
                    return false;
                }
                break;
            case LESS_THAN_OR_EQUALS:
                if (NumberUtils.isCreatable(paramValue) && NumberUtils.isCreatable(paramValue)) {
                    Double pNumber = Double.parseDouble(paramValue);
                    Double apiNumber = Double.parseDouble(apiValue);
                    if (apiNumber > pNumber) {
                        ReportUtils.fail(context, "Validation failed " + paramValue + " " + condition + " " + apiValue, StringUtils.EMPTY, false);
                        return false;
                    }
                } else {
                    ReportUtils.fail(context, "Validation failed " + paramValue + " " + condition + " " + apiValue, StringUtils.EMPTY, false);
                    return false;
                }
                break;
            case NOT_EMPTY:
            case NOT_NULL:
                if (StringUtils.isEmpty(apiValue)) {
                    ReportUtils.fail(context, "Validation failed " + currentVariant + " " + paramValue + " " + condition + " " + apiValue, StringUtils.EMPTY, false);
                    return false;
                }
                break;
        }
        return true;
    }
}
