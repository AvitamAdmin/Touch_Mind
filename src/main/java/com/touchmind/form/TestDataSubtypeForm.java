package com.touchmind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TestDataSubtypeForm extends BaseForm {
    private String shortDescription;
   // private List<String> subsidiaries;
    private String identifier;
    private String testDataType;

}
