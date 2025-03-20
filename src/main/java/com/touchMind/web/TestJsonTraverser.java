package com.touchMind.web;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;

public class TestJsonTraverser {
    public static final String $ROOT = "$.";

    public static void main(String[] args) throws FileNotFoundException {
        InputStream is = new FileInputStream(new File("addon.json"));
        DocumentContext doc = JsonPath.parse(is);
        Object rootObject = doc.read($ROOT + ".companies..models");
        printJson(rootObject);
        //System.out.println(rootObject);
    }

    private static void printJson(Object object) {
        if (object instanceof JSONArray jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                printJson(jsonArray.get(i));
            }
        } else {
            if (object instanceof LinkedHashMap) {
                System.out.println("Map");
            } else if (object instanceof String) {
                System.out.println("String");
            }
        }
    }

}
