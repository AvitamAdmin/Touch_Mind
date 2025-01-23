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
public class ReportDto extends CommonWsDto {
    private String skus;
    private List<String> sites;
    private List<String> shortcuts;
    private String subId;
    private String currentSessionId;
    private String currentNode;
    private String relationKeys;
    private String repositoryName;
    private List<String> dataSourceParams;
    private String recordId;
    private String currentSite;
    private String currentVariant;
    private String currentRelationKey;
    private String mapping;
    private String email;
    private String nodePath;
    private String skuUrl;
    private String voucherCode;
    private String skus2;
    private String category;
    private String currentPage;
    private String separator;
    private Boolean bundle;
    private String temImei;
    private Boolean enableHistory;
}
