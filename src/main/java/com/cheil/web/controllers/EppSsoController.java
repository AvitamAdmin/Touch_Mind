package com.cheil.web.controllers;

import com.cheil.core.mongo.dto.EppSsoWsDto;
import com.cheil.core.mongo.model.EppSso;
import com.cheil.core.mongo.repository.EnvironmentRepository;
import com.cheil.core.mongo.repository.SiteRepository;
import com.cheil.core.service.EppSsoService;
import com.cheil.core.service.SiteService;
import com.cheil.core.service.SubsidiaryService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/eppsso")
public class EppSsoController {

    @Autowired
    private SubsidiaryService subsidiaryService;

    @Autowired
    private EppSsoService eppSsoService;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private SiteService siteService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/show")
    public String show(Model model, HttpServletRequest request) {
        model.addAttribute("sites", siteService.findBySubsidiaryAndStatusOrderBySiteId(true));
        model.addAttribute("subsidiaries", subsidiaryService.findByStatusAndUserOrderByIdentifier(true));
        model.addAttribute("environments", environmentRepository.findByStatusOrderByIdentifier(true));
        String path = request.getServletPath();
        model.addAttribute("path", path);
        return "generator/showeppsso";
    }

    @GetMapping("/show/back")
    public String back(Model model) {
        model.addAttribute("sites", siteRepository.findAll());
        model.addAttribute("subsidiaries", subsidiaryService.findByStatusAndUserOrderByIdentifier(true));
        model.addAttribute("environments", environmentRepository.findByStatusOrderByIdentifier(true));
        model.addAttribute("path", "/eppsso/show");
        return "generator/showeppsso";
    }

    @PostMapping("/generate")
    @ResponseBody
    public EppSsoWsDto generate(@RequestBody EppSsoWsDto eppSsoWsDto) throws ParseException {
        List<EppSso> epps = new ArrayList<>();
        for (String site : eppSsoWsDto.getSites()) {
            epps.add(eppSsoService.generateSsoLink(site, eppSsoWsDto));
        }
        eppSsoWsDto.setEppSsos(modelMapper.map(epps, List.class));
        return eppSsoWsDto;
    }
}