package com.touchmind.form;

import com.touchmind.core.mongo.dto.DashboardLabelDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DashboardProfileForm extends BaseForm {
    private String identifier;
    private String description;
    private List<DashboardLabelDto> labels;
}
