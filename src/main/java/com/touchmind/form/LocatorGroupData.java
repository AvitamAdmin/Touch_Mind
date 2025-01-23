package com.touchmind.form;

import com.touchmind.core.mongo.dto.CommonDto;
import com.touchmind.core.mongo.model.LocatorPriority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class LocatorGroupData extends CommonDto {
    private List<LocatorPriority> locatorPriorityList;
    private String groupId;
    private boolean takeAScreenshot;
}
