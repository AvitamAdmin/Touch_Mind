package com.touchmind.form;

import com.touchmind.core.mongo.model.Category;
import com.touchmind.core.mongo.model.Model;
import com.touchmind.core.mongo.model.Subsidiary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class VariantForm extends BaseForm {
    private String shortDescription;
    private Model model;
    private Category category;
    private String externalProductUrl;
    private String pageType;
    private List<VariantForm> variantFormList;
    private Set<Subsidiary> subsidiaries;
}
