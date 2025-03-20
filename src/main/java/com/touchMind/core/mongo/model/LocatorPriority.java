package com.touchMind.core.mongo.model;

import com.touchMind.core.mongo.dto.LocatorGroupDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LocatorPriority {
    private String locatorId;
    private String locatorName;
    private Boolean enterKey;
    private Boolean waitForElementVisibleAndClickable;
    private Boolean checkIfElementPresentOnThePage;
    private Boolean checkIfIframe;
    private Boolean isContextData;
    private String errorMsg;
    private String groupId;
    private LocatorGroupDto locatorGroup;
}
