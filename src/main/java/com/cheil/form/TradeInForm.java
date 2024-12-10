package com.cheil.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TradeInForm extends BaseForm {
    private String fileName;
    private String startDate;
    private String endDate;
    private List<String> subAndSites;
    private List<String> sites;
    private List<String> subsidiaries;
}
