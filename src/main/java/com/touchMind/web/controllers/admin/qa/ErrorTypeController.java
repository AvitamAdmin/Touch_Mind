package com.touchMind.web.controllers.admin.qa;

import com.touchMind.core.mongo.model.ErrorType;
import com.touchMind.core.mongo.repository.ErrorTypeRepository;
import com.touchMind.core.mongotemplate.repository.QARepository;
import com.touchMind.core.service.CoreService;
import com.touchMind.form.ErrorTypeForm;
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

import java.util.List;

@Controller
@RequestMapping("/admin/errorType")
public class ErrorTypeController {

    @Autowired
    private CoreService coreService;

    @Autowired
    private QARepository qaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ErrorTypeRepository errorTypeRepository;
    @Autowired
    private QualityAssuranceService qualityAssuranceService;

    @GetMapping
    public String getErrorType(Model model) {
        List<ErrorType> errorTypes = errorTypeRepository.findAllByOrderByIdentifier();
        model.addAttribute("models", errorTypes);
        return "qa/errorType";
    }

    @GetMapping("/edit")
    public String getEditErrorType(Model model, @RequestParam(value = "id", required = false) String id) {
        model.addAttribute("editForm", modelMapper.map(errorTypeRepository.findByIdentifier(id), ErrorTypeForm.class));
        return "qa/errorTypeEdit";
    }

    @GetMapping("/add")
    public String getErrorForm(Model model) {
        model.addAttribute("editForm", new ErrorTypeForm());
        return "qa/errorTypeEdit";
    }

    @PostMapping("/edit")
    public String addLocator(Model model, @ModelAttribute("editForm") @Valid ErrorTypeForm errorTypeForm) {
        qualityAssuranceService.saveErrorTypeModel(errorTypeForm);
        return "redirect:/admin/errorType";
    }

    @GetMapping("/delete")
    public String deleteLocator(Model model, @RequestParam("id") String ids) {
        for (String id : ids.split(",")) {
            errorTypeRepository.deleteByIdentifier(id);
        }
        return "redirect:/admin/errorType";
    }
}