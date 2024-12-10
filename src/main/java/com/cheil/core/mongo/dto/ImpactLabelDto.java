package com.cheil.core.mongo.dto;

import com.cheil.form.BaseForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ImpactLabelDto extends BaseForm {
    private Set<String> labels;
    private int impact;
    private int multiplier;
}
