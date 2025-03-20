package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommonDto {
    List<SavedQueryDto> savedQueries;
    private String identifier;
    private Boolean status;
    private String shortDescription;
    private String creator;
    private Date creationTime;
    private Date lastModified;
    private String modifiedBy;
    private boolean isAdd;
}
