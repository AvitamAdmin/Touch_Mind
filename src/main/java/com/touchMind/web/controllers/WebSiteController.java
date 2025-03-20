package com.touchMind.web.controllers;

import com.touchMind.core.mongo.model.WebSite;
import com.touchMind.core.mongo.repository.WebSiteRepository;
import com.touchMind.core.service.CoreService;
import com.touchMind.form.WebSiteForm;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/admin/website")
public class WebSiteController {

    @Autowired
    private CoreService coreService;

    @Autowired
    private WebSiteRepository webSiteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public String getWebSite(Model model) {
        List<WebSite> webSites = webSiteRepository.findAllByOrderByIdentifier();
        model.addAttribute("models", webSites);
        return "website/website";
    }

    @GetMapping("/edit")
    public String getWebSite(Model model, @RequestParam(value = "id", required = false) String id) {
        WebSite webSite = webSiteRepository.findByIdentifier(id);
        WebSiteForm webSiteForm = new WebSiteForm();
        if (webSite != null) {
            webSiteForm = modelMapper.map(webSite, WebSiteForm.class);
        }
        model.addAttribute("editForm", webSiteForm);
        return "website/edit";
    }

    @GetMapping("/add")
    public String getWebsite(Model model, @ModelAttribute("editForm") WebSiteForm webSiteForm) {
        webSiteForm.setCreationTime(new Date());
        webSiteForm.setLastModified(new Date());
        webSiteForm.setStatus(true);
        webSiteForm.setCreator(coreService.getCurrentUser().getUsername());
        model.addAttribute("editForm", webSiteForm);
        return "website/edit";
    }

    @PostMapping("/edit")
    public String addWebsite(Model model, @ModelAttribute("editForm") @Valid WebSiteForm webSiteForm) {
        webSiteForm.setLastModified(new Date());
        webSiteForm.setCreator(coreService.getCurrentUser().getUsername());
        if (StringUtils.isNotEmpty(webSiteForm.getIdentifier())) {
            WebSite webSite = webSiteRepository.findByIdentifier(webSiteForm.getIdentifier());
            if (webSite != null) {
                modelMapper.map(webSiteForm, webSite);
                webSiteRepository.save(webSite);
            }
        } else {
            WebSite webSite = modelMapper.map(webSiteForm, WebSite.class);

            webSiteRepository.save(webSite);
        }
        return "redirect:/admin/website";
    }

    @GetMapping("/delete")
    public String deleteWebsite(Model model, @RequestParam("id") String ids) {
        for (String id : ids.split(",")) {
            webSiteRepository.deleteByIdentifier(id);
        }
        return "redirect:/admin/website";
    }
}
