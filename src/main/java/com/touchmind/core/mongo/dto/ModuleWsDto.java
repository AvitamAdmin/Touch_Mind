package com.touchmind.core.mongo.dto;

import com.touchmind.core.mongo.model.Model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@ToString

public class ModuleWsDto extends CommonWsDto {
    private List<ModuleDto> modules;
    private Model model;
    // private SystemDto system;
}
