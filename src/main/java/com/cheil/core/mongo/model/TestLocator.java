package com.cheil.core.mongo.model;

import com.cheil.form.LocatorSelectorDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.SortedMap;


@Document("TestLocator")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TestLocator extends CommonFields {
    public static final String DEFAULT = "default";
    private String description;
    private String methodName;
    // this should be attachable similar to dom path
    private SortedMap<String, LocatorSelectorDto> uiLocatorSelector;
    @Deprecated
    // this should be attachable similar to dom path
    private String testDataType;
    @Deprecated
    // this should be attachable similar to dom path
    private String testDataSubtype;
    @Deprecated
    //This should not be part of the locator it will attached to the locator
    private Set<String> labels;


    public LocatorSelectorDto getUiLocatorSelector(String locale) {
        return uiLocatorSelector.containsKey(locale) ? uiLocatorSelector.get(locale) != null ? uiLocatorSelector.get(locale) :
                uiLocatorSelector.get(DEFAULT) : uiLocatorSelector.get(DEFAULT);
    }

    public String getInputDataEncrypted(String locale) {
        LocatorSelectorDto locatorSelectorForm = getUiLocatorSelector(locale);
        return locatorSelectorForm != null ? locatorSelectorForm.getInputData() : "";
    }
}
