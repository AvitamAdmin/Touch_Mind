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
public class SavedQueryDto {
    private String operator;
    private String recordId;
    private String identifier;
    private String shortDescription;
    private List<SearchQueryDto> queries;
    private String sourceItem;
    private String user;
}
