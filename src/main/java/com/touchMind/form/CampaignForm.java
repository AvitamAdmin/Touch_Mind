package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CampaignForm extends BaseForm {
    private String identifier;
    private String shortDescription;
    private List<String> domPaths;
}
