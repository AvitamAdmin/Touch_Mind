package com.touchmind.web.controllers;

import com.touchmind.form.UploadForm;
import com.touchmind.storage.StorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

@RestController
public class TicketController extends BaseController {

    private static final String API_URL = "https://shop.samsung.com/de/servicesv2/getSimpleProductsInfo?productCodes=";
    private final StorageService storageService;

    public TicketController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/ticketFinder")
    @ResponseBody
    public String ticketFinder(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString(), model).build().toUri().toString())
                .collect(Collectors.toList()));
        return "ticket/ticketFinderForm";
    }

    @PostMapping("/handleTicketFinder")
    @ResponseBody
    public String handleCompareJson(@ModelAttribute("uploadForm") UploadForm uploadForm, RedirectAttributes redirectAttributes) {
        String apiUrl = uploadForm.getApiUrl();
        HttpURLConnection connection = null;
        JsonNode jsonNodeTarget = null;

        ObjectMapper objectMapper = new ObjectMapper();

        URL url = null;
        try {
            url = new URL(StringUtils.isNotEmpty(apiUrl) ? apiUrl : API_URL);
        } catch (MalformedURLException e) {
            redirectAttributes.addFlashAttribute("message", "URL Error API URL provided is incorrect");
            return "redirect:/ticketFinder";
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream responseStream = connection.getInputStream();

            jsonNodeTarget = objectMapper.readTree(responseStream);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "URL Error API URL provided is incorrect");
            return "redirect:/ticketFinder";
        }


        //redirectAttributes.addFlashAttribute("message","Json comparison completed for more details refer the Report in the link");
        //redirectAttributes.addFlashAttribute("reportFile",report.getReportFilePath());
        return "redirect:/ticketFinder";
    }

    @GetMapping("/uploadTickets")
    @ResponseBody
    public String uploadTickets(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString(), model).build().toUri().toString())
                .collect(Collectors.toList()));
        return "ticket/uploadTicketsForm";
    }

    @PostMapping("/handleUploadTickets")
    @ResponseBody
    public String handleUploadTickets(@ModelAttribute("uploadForm") UploadForm uploadForm, RedirectAttributes redirectAttributes) {
        String apiUrl = uploadForm.getApiUrl();
        HttpURLConnection connection = null;
        JsonNode jsonNodeTarget = null;

        ObjectMapper objectMapper = new ObjectMapper();

        URL url = null;
        try {
            url = new URL(StringUtils.isNotEmpty(apiUrl) ? apiUrl : API_URL);
        } catch (MalformedURLException e) {
            redirectAttributes.addFlashAttribute("message", "URL Error API URL provided is incorrect");
            return "redirect:/uploadTickets";
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream responseStream = connection.getInputStream();

            jsonNodeTarget = objectMapper.readTree(responseStream);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "URL Error API URL provided is incorrect");
            return "redirect:/uploadTickets";
        }


        //redirectAttributes.addFlashAttribute("message","Json comparison completed for more details refer the Report in the link");
        //redirectAttributes.addFlashAttribute("reportFile",report.getReportFilePath());
        return "redirect:/uploadTickets";
    }

}
