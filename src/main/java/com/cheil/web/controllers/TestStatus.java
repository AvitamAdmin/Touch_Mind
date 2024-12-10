package com.cheil.web.controllers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TestStatus {
    private int passed;
    private int failed;
    private int total;
}
