package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document("ProductPrices")
@Getter
@Setter
@NoArgsConstructor
public class ProductPrices extends CommonFields implements Serializable {
    private Double productPrice;
    private Date fromDate;
    private Date toDate;
    @DBRef(lazy = true)
    private Stock stock;
}
