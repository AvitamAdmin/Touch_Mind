package com.touchMind.mail.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.util.Map;

@ToString
@Getter
@Setter
public class EMail {
    private String to;
    private String from;
    private String subject;
    private String content;
    private Map<String, Object> model;
    private File attachment;

}