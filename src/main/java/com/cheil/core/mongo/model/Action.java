package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("Action")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Action extends CommonFields {

    private String longDescription;
    private String picEmail;
    private String systemPath;
    private System system;
    private Catalog catalog;
    private Module module;
    private List<String> subsidiaries;
    private List<String> sites;
    private String toolkitId;
    private List<String> mediaIds;
    private String role;
    private String remarks;
    private List<String> relatedActions;
}
