package com.touchMind.web.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


@RestController
public class ReportsController {

    @GetMapping("/reports")
    @ResponseBody
    public ModelAndView reports() throws IOException {
        ModelAndView model = new ModelAndView("reports");
        Map<String, List<String>> fileDateMap = new TreeMap(Comparator.reverseOrder());
        try {
            List<String> files = Files.list(Path.of(ClassLoader.getSystemResource("static/reports").toURI()))
                    .map(Path::toFile)
                    .filter(File::isFile).map(file -> file.getName()).filter(filename -> filename.contains(".html"))
                    .collect(Collectors.toList());
            Collections.reverse(files);
            for (String fileName : files) {
                String splitDate = fileName.split("_")[0];
                if (fileDateMap.containsKey(splitDate)) {
                    List<String> oldData = fileDateMap.get(splitDate);
                    oldData.add(fileName);
                    fileDateMap.put(splitDate, oldData);
                } else {
                    List<String> newData = new ArrayList<>();
                    newData.add(fileName);
                    fileDateMap.put(splitDate, newData);
                }
            }
            model.addObject("reportfiles", fileDateMap);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return model;
    }
}
