package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString

public class ModuleDto extends CommonDto {
    private String systemLink;
    private String systemPath;
    private SystemDto system;
}
