package com.touchMind.qa.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class BaseRequest {
    private Map<String, ElementActionService> actionServiceMap;
}
