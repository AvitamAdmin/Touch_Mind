package com.touchMind.core.mongo.model;

import com.touchMind.form.LocatorSelectorDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;


@Document("TestLocator")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TestLocator extends CommonFields {
    public static final String DEFAULT = "default";
    private String methodName;
    // this should be attachable similar to dom path
    private SortedMap<String, LocatorSelectorDto> uiLocatorSelector;
    @Deprecated
    //This should not be part of the locator it will attached to the locator
    private Set<String> labels;
    private String errorMsg;
    private Boolean encrypted;
    private String expression;
    private List<String> subLocators;


    public LocatorSelectorDto getUiLocatorSelector(String locale) {
        return uiLocatorSelector.containsKey(locale) ? uiLocatorSelector.get(locale) != null ? uiLocatorSelector.get(locale) :
                uiLocatorSelector.get(DEFAULT) : uiLocatorSelector.get(DEFAULT);
    }

    public String getUiLocatorSelectorToString(String locale) {
        LocatorSelectorDto locatorSelectorDto = getUiLocatorSelector(locale);
        return locatorSelectorDto != null ? StringUtils.isNotEmpty(locatorSelectorDto.getCssSelector()) ? "css: " + locatorSelectorDto.getCssSelector() :
                StringUtils.isNotEmpty(locatorSelectorDto.getXpathSelector()) ? "xpath: " + locatorSelectorDto.getXpathSelector() : "id: " +
                        locatorSelectorDto.getIdSelector() + ", Input data : " + locatorSelectorDto.getInputData() : "";
    }

    public String getInputDataEncrypted(String locale) {
        LocatorSelectorDto locatorSelectorForm = getUiLocatorSelector(locale);
        return locatorSelectorForm != null ? locatorSelectorForm.getInputData() : "";
    }
}
