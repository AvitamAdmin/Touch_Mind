package com.touchmind.core.mongo.dto;

import com.touchmind.core.mongo.model.Category;
import com.touchmind.core.mongo.model.Model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class VariantDto extends CommonDto {
    private String externalProductUrl;
    private Model model;
    private Category category;
    private String pageType;
    private List<String> subsidiaries;
}
