package com.cheil.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DataSourceForm extends BaseForm {
    private String shortDescription;
    private String dataSourceId;
    private String format;
    private String sourceAddress;
    private List<String> srcInputParams;
    private List<DataSourceInputForm> inputForms;
    private String skuUrl;
    private String separatorSymbol;
}
