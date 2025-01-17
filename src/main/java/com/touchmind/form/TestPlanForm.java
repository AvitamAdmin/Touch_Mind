package com.touchmind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestPlanForm extends BaseForm {
    private ObjectId objectId;
    private String identifier;
    private String shortDescription;
    private List<ObjectId> testLocatorGroups;
   // private String subsidiary;
    private List<TestPlanForm> testPlanFormList;
}
