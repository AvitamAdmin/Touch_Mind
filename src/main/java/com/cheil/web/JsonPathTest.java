package com.cheil.web;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.io.File;
import java.io.IOException;

public class JsonPathTest {
    public static void main(String[] args) throws IOException {
        File file = new File("jsonExample.json");
        DocumentContext doc = JsonPath.parse(file);
        JSONArray array = doc.read("$.[*].products modelcode");
        for (int i = 0; i < array.size(); i++) {
            System.out.println("-------------------");
            Object o = JsonPath.read(array.get(i).toString(), "$..price.value");
            System.out.println(o);
        }
        // Object obk = doc.read("$.[0].code");
        // System.out.println(obk);

    }
}
