package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProductPricesDto extends CommonDto {
    private Double productPrice;
    private Date fromDate;
    private Date toDate;
    private StockDto stock;
}
