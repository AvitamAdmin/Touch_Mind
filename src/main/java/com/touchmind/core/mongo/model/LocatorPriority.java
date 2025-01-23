package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LocatorPriority {
    private Long priority;
    private String locatorId;
    private Boolean enterKey;
    private Boolean waitForElementVisibleAndClickable;
    private Boolean checkIfElementPresentOnThePage;
    private Boolean checkIfIframe;
    private Boolean isContextData;
    private String errorMsg;
    private String groupId;
}
