package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SiteForm extends BaseForm {
    private String shortDescription;
  //  private Subsidiary subsidiary;
    private String affiliateId;
    private String affiliateName;
    private String siteChannel;
    private String secretKey;
    private List<SiteForm> siteFormList;
}
