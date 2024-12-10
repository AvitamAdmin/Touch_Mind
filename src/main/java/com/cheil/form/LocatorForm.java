package com.cheil.form;

import com.cheil.core.mongo.dto.LocatorGroupDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.SortedMap;

@Getter
@Setter
@NoArgsConstructor
public class LocatorForm extends BaseForm {
    List<LocatorGroupDto> testLocatorGroups;

    private String identifier;
    private String description;

    private String methodName;

    private SortedMap<String, LocatorSelectorDto> uiLocatorSelector;
    private String testDataType;
    private String testDataSubtype;
    private List<String> labels;
    private List<LocatorForm> locatorFormList;
    private String groupId;
    private String errorMsg;
}
