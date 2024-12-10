package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("SourceTargetMapping")
@Getter
@Setter
@NoArgsConstructor
public class SourceTargetMapping extends CommonFields {
    private List<String> subsidiaries;
    private String node;
    private String dataRelation;
    private List<SourceTargetParamMapping> sourceTargetParamMappings;
    private Boolean enableVoucher;
    private Boolean enableCategory;
    private Boolean enableCurrentPage;
    private Boolean enableToggle;
    private Boolean enableVariant;
}
