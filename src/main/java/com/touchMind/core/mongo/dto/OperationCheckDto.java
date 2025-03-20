package com.touchMind.core.mongo.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OperationCheckDto extends CommonDto {
    private String shortcutName;
    private String shortcutValue;
}
