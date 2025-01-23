package com.touchmind.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RestController
public class ToolkitController extends BaseController {

    @GetMapping("/toolkit")
    @ResponseBody
    public ModelAndView ticketFinder() throws IOException {
        return new ModelAndView("toolkit/toolkitContent");
    }
}
