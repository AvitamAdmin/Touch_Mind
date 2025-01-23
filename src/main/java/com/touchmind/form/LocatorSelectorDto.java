package com.touchmind.form;

import com.touchmind.core.mongo.dto.CommonDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocatorSelectorDto extends CommonDto {
    private String xpathSelector;
    private String cssSelector;
    private String idSelector;
    private String othersSelector;
    private String inputData;
    private String errorMsg;
    private String expression;
    private List<String> subLocators;
}
