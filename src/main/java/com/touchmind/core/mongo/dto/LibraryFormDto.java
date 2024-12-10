package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LibraryFormDto extends CommonDto {
    private String subsidiaryId;
    private LibraryDto child;
    private String errorType;
    private String type;
    private String paragraph;
    private String errorMsg;
    private String resolution;
    private String longDescription;
    private String picEmail;
    private String systemLink;
    private String systemPath;
    private String systemId;
    private String toolkitId;
    private String catalogId;
    private String moduleId;
    private SubsidiaryDto subsidiary;
    private List<String> subsidiaries;
    private List<String> catalogs;
    private List<String> modules;
    private List<String> sites;
    private List<String> actions;
    private List<String> medias;
    private List<String> subLibraries;
    private MultipartFile[] files;
    private List<String> subAndSites;
    private String role;
    private List<String> remarks;
    private List<String> dummyRemarks;
    private List<String> relatedActions;
    private List<LibraryFormDto> libraryFormList;
}
