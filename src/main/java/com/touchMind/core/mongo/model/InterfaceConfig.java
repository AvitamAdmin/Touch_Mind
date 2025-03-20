package com.touchMind.core.mongo.model;

import com.touchMind.core.mongo.dto.SearchDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("InterfaceConfig")
@Getter
@Setter
@NoArgsConstructor
public class InterfaceConfig extends CommonFields {
    private String node;
    private List<SearchDto> attributes;
}
