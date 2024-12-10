package com.cheil.core.mongo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Arrays;

@Document("Stock")
@Getter
@Setter
@NoArgsConstructor
public class Stock extends CommonFields implements Serializable {
    @JsonIgnore
    private String productCode;
    @JsonIgnore
    private String stockLevelStatus;
    @JsonIgnore
    private String stockLevelStatusDisplay;
    @JsonIgnore
    private String salesStatus;
    @JsonIgnore
    private String price;
    @JsonIgnore
    private String priceFormatted;
    @JsonIgnore
    private String promotionPrice;
    @JsonIgnore
    private String promotionPriceFormatted;
    @JsonIgnore
    private String resultCode;
    @JsonIgnore
    private String resultMessage;
    @JsonIgnore
    private String reserveNowBtnYn;
    @JsonIgnore
    private String[] aemAdditionalInfoTypes;
    @JsonIgnore
    private String flagPreOrder;
    @JsonIgnore
    private String supportedAvailableServices;
    @JsonIgnore
    private int stockLevel;
    @JsonIgnore
    private Double discountPercent;
    @JsonIgnore
    private String site;
    @JsonIgnore
    private String sessionId;

    @Override
    public String toString() {
        return "Stock{" +
                ", productCode='" + productCode + '\'' +
                ", stockLevelStatus='" + stockLevelStatus + '\'' +
                ", stockLevelStatusDisplay='" + stockLevelStatusDisplay + '\'' +
                ", salesStatus='" + salesStatus + '\'' +
                ", price='" + price + '\'' +
                ", priceFormatted='" + priceFormatted + '\'' +
                ", promotionPrice='" + promotionPrice + '\'' +
                ", promotionPriceFormatted='" + promotionPriceFormatted + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", resultMessage='" + resultMessage + '\'' +
                ", reserveNowBtnYn='" + reserveNowBtnYn + '\'' +
                ", aemAdditionalInfoTypes=" + Arrays.toString(aemAdditionalInfoTypes) +
                ", flagPreOrder='" + flagPreOrder + '\'' +
                ", supportedAvailableServices='" + supportedAvailableServices + '\'' +
                ", stockLevel=" + stockLevel +
                ", discountPercent=" + discountPercent +
                ", site='" + site + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
