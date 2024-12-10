package com.touchmind.form;

import com.touchmind.core.mongo.model.Model;
import com.touchmind.core.mongo.model.Subsidiary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CategoryForm extends BaseForm {
    private String parentId;
    private List<String> childId;
    private String shortDescription;
    private Set<Model> models;
    private Set<Subsidiary> subsidiaries;
    private List<CategoryForm> categoryFormList;
}
