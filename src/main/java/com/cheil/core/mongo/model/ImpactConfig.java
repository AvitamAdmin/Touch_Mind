package com.cheil.core.mongo.model;

import com.cheil.core.mongo.dto.ImpactLabelDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("ImpactConfig")
@Getter
@Setter
@NoArgsConstructor
public class ImpactConfig extends CommonFields {
    private List<ImpactLabelDto> labels;
}
