package com.touchMind.web.controllers.toolkit;

import com.touchMind.core.mongo.dto.CategoryDto;
import com.touchMind.core.mongo.dto.ModelDto;
import com.touchMind.core.mongo.dto.SiteDto;
import com.touchMind.core.mongo.repository.CategoryRepository;
import com.touchMind.core.mongo.repository.ModelRepository;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.service.SiteService;
import com.touchMind.form.ToolkitForm;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/toolkit/import")
public class ImportController extends BaseController {

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private SiteService siteService;

//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    @ResponseBody
    public ModelAndView getImportForm(Model model) {
        model.addAttribute("editForm", new ToolkitForm());
        model.addAttribute("models", modelRepository.findAll().stream().filter(data -> data.getStatus()).collect(Collectors.toList()));
        model.addAttribute("sites", siteService.findBySubsidiaryAndStatusOrderBySiteId(true).stream().filter(data -> data.getStatus()).collect(Collectors.toList()));
        return new ModelAndView("toolkit/import/import");
    }

    @RequestMapping(value = "/getSitesForSubsidiary/{subId}", method = RequestMethod.GET)
    public @ResponseBody List<SiteDto> getSites(@RequestBody @PathVariable("subId") String subId) {
        Type listType = new TypeToken<List<SiteDto>>() {
        }.getType();
        return modelMapper.map(siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(subId, true), listType);
    }

    @RequestMapping(value = "/getCategoriesForSubsidiary/{subId}", method = RequestMethod.GET)
    public @ResponseBody List<CategoryDto> getCategories(@RequestBody @PathVariable("subId") String subId) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatusAndSubsidiariesOrderByIdentifier(true, subId), listType);
    }

    @RequestMapping(value = "/getModelForSubsidiary/{subId}", method = RequestMethod.GET)
    public @ResponseBody List<ModelDto> getModelForSubsidiary(@RequestBody @PathVariable("subId") String subId) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        return modelMapper.map(modelRepository.findByStatusAndSubsidiariesOrderByIdentifier(true, subId), listType);
    }
}
