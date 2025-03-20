package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EppSsoDto extends CommonDto {
    private String ssoLink;
    private String affiliateId;
    private String hash;
    private String timestamp;
    private String disabledLink;
}
