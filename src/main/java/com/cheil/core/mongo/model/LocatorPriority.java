package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LocatorPriority {
    private Long priority;
    private List<String> locators;
    private Boolean enterKey;
    private Boolean waitForElementVisibleAndClickable;
    private Boolean checkIfElementPresentOnThePage;
    private Boolean checkIfIframe;
    private Boolean isContextData;
    private String errorMsg;
}
