package com.cheil.web;

import java.util.List;

public class RegExTest {
    public static void main(String[] args) {
        List<String> keys = List.of(
                "product_lowestInstallmentAmount",
                "product_stock_stockLevel",
                "products_modelcode_0",
                "product_stock_stockLevelStatus",
                "products_stock_stockLevelStatus_0",
                "products_price_value_0",
                "product_supportedAvailableServices",
                "product_price_value, salesStatus");
        List<String> pValues = List.of(
                "temSite",
                "temVariant",
                "products_modelcode",
                "products_stock_stockLevelStatus",
                "salesStatus",
                "product_lowestInstallmentAmount",
                "product_price_value",
                "product_stock_stockLevel");
        System.out.println("I end with a number _554".matches("^.*\\d$"));
    }
}
