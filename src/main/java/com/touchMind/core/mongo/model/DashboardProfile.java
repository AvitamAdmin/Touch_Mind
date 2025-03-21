package com.touchMind.core.mongo.model;

import com.touchMind.core.mongo.dto.DashboardLabelDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("DashboardProfile")
@Getter
@Setter
@NoArgsConstructor
public class DashboardProfile extends CommonFields {

    private String displayName;
    private List<DashboardLabelDto> labels;
}
