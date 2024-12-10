package com.touchmind.form;

import com.touchmind.core.mongo.model.Subsidiary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PromotionForm extends BaseForm {
    private String skus;
    private List<String> sites;
    private List<String> shortcuts;
    private Subsidiary subsidiary;
    private String version;
    private String condition;
    private String actions;
    private String fromDate;
    private String toDate;
}
