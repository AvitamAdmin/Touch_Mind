package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("CrawlerPaths")
@Getter
@Setter
@NoArgsConstructor
public class CrawlerPath extends CommonFields {
    private String pathCategory;
    private String crawlerPath;
    private String pattern;
    private List<String> sites;
    private String subsidiaryIdentifier;
}
