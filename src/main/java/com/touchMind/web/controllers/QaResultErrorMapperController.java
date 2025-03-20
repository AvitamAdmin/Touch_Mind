package com.touchMind.web.controllers;

import com.touchMind.core.mongo.model.QaResultErrorMapper;
import com.touchMind.core.mongo.model.QaResultErrorMapperRepository;
import com.touchMind.core.mongo.repository.ErrorTypeRepository;
import com.touchMind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchMind.core.service.CoreService;
import com.touchMind.form.QaResultErrorMapperForm;
import com.touchMind.qa.service.QualityAssuranceService;
import jakarta.validation.Valid;
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
@RequestMapping("/admin/qaErrorMapper")
public class QaResultErrorMapperController {

    @Autowired
    private CoreService coreService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private QaResultErrorMapperRepository qaResultErrorMapperRepository;

    @Autowired
    private ErrorTypeRepository errorTypeRepository;

    @Autowired
    private QualityAssuranceService qualityAssuranceService;

    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;

    @GetMapping
    public String getImpactConfigs(Model model) {
        List<QaResultErrorMapper> impactConfigs = qaResultErrorMapperRepository.findAllByOrderByIdentifier();
        model.addAttribute("models", impactConfigs);
        return "qa/qaErrorMapper";
    }

    @GetMapping("/edit")
    public String getErrorMapper(Model model, @RequestParam(value = "id", required = false) String id) {
        QaResultErrorMapper qaResultErrorMapper = qaResultErrorMapperRepository.findByIdentifier(id);
        QaResultErrorMapperForm qaResultErrorMapperForm = new QaResultErrorMapperForm();
        if (qaResultErrorMapper != null) {
            qaResultErrorMapperForm = modelMapper.map(qaResultErrorMapper, QaResultErrorMapperForm.class);
        }
        model.addAttribute("testCases", testLocatorGroupRepository.findAllByOrderByIdentifier());
        model.addAttribute("editForm", qaResultErrorMapperForm);
        model.addAttribute("errorTypes", errorTypeRepository.findAllByOrderByIdentifier());
        return "qa/qaErrorMapperEdit";
    }

    @GetMapping("/add")
    public String getErrorMapper(Model model) {
        QaResultErrorMapperForm qaResultErrorMapperForm = new QaResultErrorMapperForm();
        qaResultErrorMapperForm.setCreationTime(new Date());
        qaResultErrorMapperForm.setLastModified(new Date());
        qaResultErrorMapperForm.setStatus(true);
        qaResultErrorMapperForm.setCreator(coreService.getCurrentUser().getUsername());
        model.addAttribute("editForm", qaResultErrorMapperForm);
        model.addAttribute("testCases", testLocatorGroupRepository.findAllByOrderByIdentifier());
        model.addAttribute("errorTypes", errorTypeRepository.findAllByOrderByIdentifier());
        return "qa/qaErrorMapperEdit";
    }

    @PostMapping("/edit")
    public String addLocator(Model model, @ModelAttribute("editForm") @Valid QaResultErrorMapperForm qaResultErrorMapperForm) {
        qualityAssuranceService.saveErrorMapperModel(qaResultErrorMapperForm);
        return "redirect:/admin/qaErrorMapper";
    }

    @GetMapping("/delete")
    public String deleteDashboardProfile(Model model, @RequestParam("id") String ids) {
        for (String id : ids.split(",")) {
            qaResultErrorMapperRepository.deleteByIdentifier(id);
        }
        return "redirect:/admin/qaErrorMapper";
    }
}
