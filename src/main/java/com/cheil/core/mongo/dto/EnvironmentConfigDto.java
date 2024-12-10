package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EnvironmentConfigDto {
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
