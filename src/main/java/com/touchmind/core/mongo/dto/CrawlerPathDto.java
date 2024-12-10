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
public class CrawlerPathDto extends CommonDto {
    private String pathCategory;
    private String crawlerPath;
    private String pattern;
    private List<String> sites;
    private String subsidiaryIdentifier;
}
