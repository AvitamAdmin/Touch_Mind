package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CrawlerPathForm extends BaseForm {
    private String pathCategory;
    private String crawlerPath;
    private List<String> sites;
    private String pattern;
}
