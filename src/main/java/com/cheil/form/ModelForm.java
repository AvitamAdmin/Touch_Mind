package com.cheil.form;

import com.cheil.core.mongo.model.Category;
import com.cheil.core.mongo.model.Site;
import com.cheil.core.mongo.model.Subsidiary;
import com.cheil.core.mongo.model.Variant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ModelForm extends BaseForm {
    private String shortDescription;
    private Site site;
    private Set<Variant> variants;
    private Set<Category> categories;
    private Set<Subsidiary> subsidiaries;
    private List<ModelForm> modelFormList;
}
