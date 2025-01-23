package com.touchmind.web.controllers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataTable<T> {
    private int draw;
    private int start;
    private int length;
    private long recordsTotal;
    private long recordsFiltered;
    private T content;
}
