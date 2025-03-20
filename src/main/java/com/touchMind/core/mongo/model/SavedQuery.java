package com.touchMind.core.mongo.model;

import com.touchMind.core.mongo.dto.SearchQueryDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("SavedQuery")
@Getter
@Setter
public class SavedQuery extends CommonFields {
    private String operator;
    private List<SearchQueryDto> queries;
    private String sourceItem;
    private String user;
}
