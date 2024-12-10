package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EnvironmentConfig {
    private String url;
    private String loginName;
    private String loginPassword;
    private String loginNameUiSelector;
    private String loginPasswordSelector;
    private String actionElement;
    private String shortDescription;
    private Boolean waitAfterUrl;
    private Boolean waitBeforeUrl;
    private Boolean waitAfterClick;
    private Boolean waitBeforeClick;
}
