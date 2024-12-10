package com.cheil.form;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronJobProfileForm extends BaseForm {

    private String identifier;
    private String recipients;
}
