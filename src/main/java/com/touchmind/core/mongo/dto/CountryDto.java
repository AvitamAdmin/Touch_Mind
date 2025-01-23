package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CountryDto extends CommonDto {
    private Locale locale;
}
