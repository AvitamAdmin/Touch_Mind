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
public class SourceTargetMappingDto extends CommonDto {
    private List<String> subsidiaries;
    private String node;
    private String dataRelation;
    private List<SourceTargetParamMappingDto> sourceTargetParamMappings;
    private boolean enableVoucher;
    private boolean enableCategory;
    private boolean enableCurrentPage;
    private boolean enableToggle;
    private boolean enableVariant;
    private boolean enableSkus;
}
