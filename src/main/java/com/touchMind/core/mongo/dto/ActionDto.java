package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ActionDto extends CommonDto {
    private String longDescription;
    private String picEmail;
    private String systemPath;
    private SystemDto system;
    private CatalogDto catalog;
    private ModuleDto module;
    private List<String> subsidiaries;
    private List<String> sites;
    private String toolkitId;
    private List<String> mediaIds;
    private String role;
    private String remarks;
    private List<String> relatedActions;
}
