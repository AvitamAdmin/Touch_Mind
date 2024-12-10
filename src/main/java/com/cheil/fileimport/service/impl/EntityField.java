package com.cheil.fileimport.service.impl;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EntityField {
    String value;
    Map<String, String> attributes;
}
