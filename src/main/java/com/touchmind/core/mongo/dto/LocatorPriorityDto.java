package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LocatorPriorityDto extends CommonDto {
    private Long priority;
    private String locatorId;
    private Boolean enterKey;
    private Boolean waitForElementVisibleAndClickable;
    private Boolean checkIfElementPresentOnThePage;
    private Boolean checkIfIframe;
    private Boolean isContextData;
    private String errorMsg;
}
