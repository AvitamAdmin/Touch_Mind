package com.touchmind.form;

import com.touchmind.core.mongo.model.Category;
import com.touchmind.core.mongo.model.Country;
import com.touchmind.core.mongo.model.Model;
import com.touchmind.core.mongo.model.Site;
import com.touchmind.core.mongo.model.User;
import com.touchmind.core.mongo.model.Variant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class SubsidiaryForm extends BaseForm {
    private String cluster;
    private Set<Site> sites;
    private Set<Model> models;
    private Set<Variant> variants;
    private Set<User> users;
    private Set<Category> categories;
    private String isoCode;
    private Country language;
    private String shortDescription;
    private List<SubsidiaryForm> subsidiaryFormList;
}
