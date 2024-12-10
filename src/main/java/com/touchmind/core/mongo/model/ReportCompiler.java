package com.touchmind.core.mongo.model;

import com.touchmind.core.mongo.dto.ReportCompilerMappingDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("ReportCompiler")
@Getter
@Setter
@NoArgsConstructor
public class ReportCompiler extends CommonFields {
    private String node;
    private String dataRelation;
    private List<String> reportInterfaces;
    private List<ReportCompilerMappingDto> reportCompilerMappings;
}
