package com.cheil.web.controllers.toolkit;

import com.cheil.core.mongo.repository.ModelRepository;
import com.cheil.core.service.SiteService;
import com.cheil.form.ModelForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequestMapping("/toolkit/models")
public class ToolkitModelController {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private SiteService siteService;


    @GetMapping
    @ResponseBody
    public ModelAndView getModels(Model model) {
        model.addAttribute("editForm", new ModelForm());
        model.addAttribute("models", modelRepository.findAll());
        model.addAttribute("sites", siteService.findBySubsidiaryAndStatusOrderBySiteId(true));
        return new ModelAndView("toolkit/models/models");
    }


    @PostMapping("/process")
    @ResponseBody
    public String processModels() {
        return "toolkit/models/models";
    }
}
