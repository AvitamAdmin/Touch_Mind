package com.cheil.form;

import com.cheil.core.mongo.dto.ReportCompilerMappingDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReportCompilerForm extends BaseForm {
    private String node;
    private String identifier;
    private String description;
    private String dataRelation;
    private List<String> reportInterfaces;
    private List<ReportCompilerMappingDto> reportCompilerMappings;
}
