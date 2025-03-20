package com.touchMind.web.controllers;

import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.model.User;
import com.touchMind.core.mongo.repository.CronHistoryRepository;
import com.touchMind.core.service.NodeService;
import com.touchMind.core.service.UserService;
import com.touchMind.web.controllers.toolkit.BaseController;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;


@RestController
public class HomeController extends BaseController {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CronHistoryRepository cronHistoryRepository;

    @Autowired
    private Environment env;

    @GetMapping("/home")
    @ResponseBody
    public ModelAndView home(HttpSession session, Model model) {
        model.addAttribute("nodes", userService.isAdminRole() ? nodeService.getAllNodes() : nodeService.getNodesForRoles());
        String currentUserSession = (String) session.getAttribute("currentUserSession");
        UUID uuid = UUID.randomUUID();
        model.addAttribute("pageTitle", env.getProperty("site.title"));
        model.addAttribute("currentUserSession", StringUtils.isNotEmpty(currentUserSession) ? currentUserSession : uuid.toString());
        model.addAttribute("cronJobs", cronHistoryRepository.findAll());
        return new ModelAndView("home");
    }

    @GetMapping("/")
    @ResponseBody
    public ModelAndView userHome(HttpSession session, Model model) {
        model.addAttribute("nodes", userService.isAdminRole() ? nodeService.getAllNodes() : nodeService.getNodesForRoles());
        User user = userService.getCurrentUser();
        if (StringUtils.isNotEmpty(user.getNode())) {
            Node node = nodeService.findById(user.getNode());
            if (node != null) {
                model.addAttribute("path", node.getPath());
                return new ModelAndView("userHome");
            }
        }
        model.addAttribute("defaultHomePage", true);
        return home(session, model);
    }
}