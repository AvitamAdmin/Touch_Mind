package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LibraryDto extends CommonDto {
    private String errorType;
    private String type;
    private String errorMsg;
    private String resolution;
    private String paragraph;
    private String example;
    private String subsidiary;
    private List<String> subsidiaries;
    private List<String> sites;
    private List<String> actions;
    private List<String> medias;
    private List<String> subLibraries;
    private String picEmail;
    private List<String> actionRemarks;
}
