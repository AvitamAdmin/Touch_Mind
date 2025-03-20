package com.touchMind.web.controllers;

import com.touchMind.core.json.JsonDiffService;
import com.touchMind.excel.ExcelFileCompareService;
import com.touchMind.form.UploadForm;
import com.touchMind.storage.StorageFileNotFoundException;
import com.touchMind.storage.StorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;


@RestController
public class FileUploadController {


    private static final String API_URL = "https://p1-smn2-api-cdn.shop.samsung.com/tokocommercewebservices/v2/de/carriers/device/SM-S908BZKDEUB/plans?fields=DEFAULT";
    private final StorageService storageService;
    @Autowired
    private ExcelFileCompareService excelFileCompareService;
    @Autowired
    private JsonDiffService jsonDiffService;


    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/list")
    @ResponseBody
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString(), model).build().toUri().toString())
                .collect(Collectors.toList()));
        return "uploadForm";
    }

    @GetMapping("/tariff")
    @ResponseBody
    public String tariffFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString(), model).build().toUri().toString())
                .collect(Collectors.toList()));

        return "tariffContent";
    }

    @GetMapping("/files")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@RequestParam String filename) {

        try {
            Resource file = storageService.loadAsResource(filename);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound() {
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/handleCompareExcel")
    @ResponseBody
    public String handleCompareCSV(@ModelAttribute("uploadForm") UploadForm uploadForm, RedirectAttributes redirectAttributes) throws IOException {
        storageService.store(uploadForm.getFile());
        storageService.store(uploadForm.getFile2());
        String fileOne = storageService.getRootLocation() + File.separator + uploadForm.getFile().getOriginalFilename();
        String fileTwo = storageService.getRootLocation() + File.separator + uploadForm.getFile2().getOriginalFilename();
        excelFileCompareService.compareTwoExcelFiles(fileOne, fileTwo, uploadForm.getFile().getOriginalFilename(), uploadForm.getFile2().getOriginalFilename());

        redirectAttributes.addFlashAttribute("message", "Json comparison completed for more details refer the Report in the link");
        //redirectAttributes.addFlashAttribute("reportFile", report.getReportFilePath());
        return "redirect:/compareExcel";
    }

    @GetMapping("/compareexcel")
    @ResponseBody
    public String compareCSV(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString(), model).build().toUri().toString())
                .collect(Collectors.toList()));
        return "compareExcelContent";
    }

    @GetMapping("/comparejson")
    @ResponseBody
    public String compareJson(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString(), model).build().toUri().toString())
                .collect(Collectors.toList()));
        return "compareJsonContent";
    }

    @PostMapping("/handleCompareJson")
    @ResponseBody
    public String handleCompareJson(@ModelAttribute("uploadForm") UploadForm uploadForm, RedirectAttributes redirectAttributes) {
        storageService.store(uploadForm.getFile());
        String fileOne = storageService.getRootLocation() + File.separator + uploadForm.getFile().getOriginalFilename();
        String apiUrl = uploadForm.getApiUrl();
        HttpURLConnection connection = null;
        JsonNode jsonNodeSource = null;
        JsonNode jsonNodeTarget = null;
        //report.reportStart("Json comparison with File => " + fileOne);

        ObjectMapper objectMapper = new ObjectMapper();

        URL url = null;
        try {
            url = new URL(StringUtils.isNotEmpty(apiUrl) ? apiUrl : API_URL);
        } catch (MalformedURLException e) {
            //report.reportFailed("URL Error", "API URL provided is incorrect");
            redirectAttributes.addFlashAttribute("message", "URL Error API URL provided is incorrect");
            //redirectAttributes.addFlashAttribute("reportFile", report.getReportFilePath());
            return "redirect:/comparejson";
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream responseStream = connection.getInputStream();
            File file1 = new File(fileOne);
            jsonNodeSource = objectMapper.readTree(file1);
            jsonNodeTarget = objectMapper.readTree(responseStream);
        } catch (IOException e) {
            //report.reportFailed("URL Error", "API URL provided is incorrect");
            redirectAttributes.addFlashAttribute("message", "URL Error API URL provided is incorrect");
            //redirectAttributes.addFlashAttribute("reportFile", report.getReportFilePath());
            return "redirect:/comparejson";
        }


        if (jsonNodeSource != null || jsonNodeTarget != null) {
            boolean result = jsonDiffService.compareJsonFiles(jsonNodeSource, jsonNodeTarget);
        } else {
            //report.reportFailed("File", "Error processing file , could not compare the File with remote API");
        }
        //report.reportFlush();

        redirectAttributes.addFlashAttribute("message", "Json comparison completed for more details refer the Report in the link");
        //redirectAttributes.addFlashAttribute("reportFile", report.getReportFilePath());
        return "redirect:/comparejson";
    }

    @GetMapping("/userRegistration")
    @ResponseBody
    public String userRegistration() throws IOException {
        return "userRegistrationContent";
    }

    @GetMapping("/paymentCheck")
    @ResponseBody
    public String paymentCheck() {
        return "paymentCheckContent";
    }

    @GetMapping("/userOrder")
    @ResponseBody
    public String userOrder() throws IOException {
        return "userOrderContent";
    }

    @GetMapping("/promotion")
    @ResponseBody
    public String promotion() throws IOException {
        return "promotionContent";
    }

}
