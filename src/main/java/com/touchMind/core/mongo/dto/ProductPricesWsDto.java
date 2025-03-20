package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProductPricesWsDto extends CommonWsDto {
    private List<ProductPricesDto> productPricesList;
}
