package com.touchMind.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/localize")
public class LocalizationController {

    @Autowired
    ResourceBundleMessageSource messageSource;

    @GetMapping()
    @ResponseBody
    public String getMessage(@RequestParam("code") String code) {
        return messageSource.getMessage(code, null, Locale.ENGLISH);
    }
}