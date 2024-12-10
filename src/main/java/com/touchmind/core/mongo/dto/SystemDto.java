package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SystemDto extends CommonDto {
    private String systemLink;
    private String systemPath;
    private List<String> subsidiaries;
    private List<String> catalogs;
    private List<String> modules;
}
