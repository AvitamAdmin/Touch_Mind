package com.touchmind.form;

import com.touchmind.core.mongo.model.Subsidiary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ToolkitForm extends BaseForm {
    private String skus;
    private List<String> sites;
    private List<String> shortcuts;
    private Subsidiary subsidiary;
    private String voucherCode;
    private String timeZone;
    private String errorType;
    private String errorMsg;
    private String skus2;
    private String category;
    private Boolean bundle;
    private String currentPage;
    private String ssoDate;
    private String siteIsoCode;
    private Boolean isDebug;
    private String environment;
}
