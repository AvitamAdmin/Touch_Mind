package com.touchMind.web.controllers;

import java.util.Map;

public interface WebConstants {
    String CHECK_STOCK = "CheckStock";
    String CHECK_PRICE = "CheckPrice";
    String CHECK_ADD_ON = "CheckAddOn";
    String CHECK_TRADE_IN = "CheckTradeIn";
    String CHECK_SIM = "CheckSIM";
    String CHECKE_WARRANTY = "CheckeWarranty";
    String CHECK_EUP_1 = "CheckEUP1";

    Map<String, String> mapping = Map.of(
            CHECK_STOCK, "redirect:/toolkit/stockReport"
            , CHECK_PRICE, "redirect:/toolkit/priceReport"
            , CHECK_ADD_ON, "redirect:/toolkit/addOnsReport"
            , CHECK_TRADE_IN, "redirect:/toolkit/tradeInReport"
            , CHECK_SIM, "redirect:/toolkit/simPlanReport"
            , CHECKE_WARRANTY, "redirect:/toolkit/eWarrantyReport"
            , CHECK_EUP_1, "redirect:/toolkit/euptReport"
    );
}
