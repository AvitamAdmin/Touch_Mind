package com.touchmind.core.mongo.dto;

import com.touchmind.form.LocatorSelectorDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestLocatorDto extends CommonDto {
    private String methodName;
    private SortedMap<String, LocatorSelectorDto> uiLocatorSelector;
    private Map<String, String> inputData;
//    private String testDataType;
//    private String testDataSubtype;
    private Set<String> labels;
    private String errorMsg;
}
