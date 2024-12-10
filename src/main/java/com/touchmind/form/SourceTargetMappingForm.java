package com.touchmind.form;

import com.touchmind.core.mongo.model.SourceTargetParamMapping;
import com.touchmind.core.mongo.model.Subsidiary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SourceTargetMappingForm extends BaseForm {
    private String sourceTargetId;
    private String shortDescription;
    private List<Subsidiary> subsidiaries;
    private String node;
    private String dataRelation;
    private List<SourceTargetParamMapping> sourceTargetParamMappingList;
    private Boolean enableVoucher;
    private Boolean enableCategory;
    private Boolean enableCurrentPage;
    private Boolean enableToggle;
    private Boolean enableVariant;
}
