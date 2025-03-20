package com.touchMind.form;

import com.touchMind.core.mongo.model.SourceTargetParamMapping;
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
  //  private List<Subsidiary> subsidiaries;
    private String node;
    private String dataRelation;
    private List<SourceTargetParamMapping> sourceTargetParamMappingList;
    private Boolean enableVoucher;
    private Boolean enableCategory;
    private Boolean enableCurrentPage;
    private Boolean enableToggle;
    private Boolean enableVariant;
}
