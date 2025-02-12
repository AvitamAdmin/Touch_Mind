package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommonDto {
    List<SavedQueryDto> savedQueries;
    //TODO WARNING!!! recordId problematic while processing using streams do not use in future and if possible remove from old usages
    private String recordId;
    private String identifier;
    private Boolean status;
    private String shortDescription;
    private String creator;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date creationTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastModified;
    private String modifiedBy;
}
