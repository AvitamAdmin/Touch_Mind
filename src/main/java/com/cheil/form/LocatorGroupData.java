package com.cheil.form;

import com.cheil.core.mongo.model.ConditionGroup;
import com.cheil.core.mongo.model.LocatorPriority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class LocatorGroupData extends BaseForm {
    private List<LocatorPriority> locatorPriorityList;
    private String groupId;
    private List<ConditionGroup> conditionGroupList;
    private boolean checkEppSso;
    private boolean takeAScreenshot;
}
