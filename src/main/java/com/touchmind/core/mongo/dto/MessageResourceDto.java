package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MessageResourceDto extends CommonDto {
    private String testPlanId;
    private String description;
    private Integer percentFailure;
    private String recipients;
    private String type;
}
