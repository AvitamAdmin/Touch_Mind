package com.touchMind.form;

import com.touchMind.core.mongo.dto.ImpactLabelDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ImpactConfigForm extends BaseForm {
    private String identifier;
    private List<ImpactLabelDto> labels;
}
