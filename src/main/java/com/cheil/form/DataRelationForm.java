package com.cheil.form;

import com.cheil.core.mongo.model.DataRelationParams;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DataRelationForm extends BaseForm {
    private String dataRelationId;
    private String shortDescription;
    private List<DataRelationParams> dataRelationParams;
    private Boolean enableGenerator;
}
