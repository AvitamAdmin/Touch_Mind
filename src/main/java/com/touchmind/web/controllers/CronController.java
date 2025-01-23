package com.touchmind.web.controllers;

import com.touchmind.core.mongo.model.CronHistory;
import com.touchmind.core.mongo.repository.CronHistoryRepository;
import com.touchmind.core.mongotemplate.repository.QARepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/")
public class CronController extends BaseController {

    Logger logger = LoggerFactory.getLogger(CronController.class);
    @Autowired
    private Environment env;

    @Autowired
    private QARepository qaRepository;

    @Autowired
    private CronHistoryRepository cronHistoryRepository;

    @Value("${reports.passed.delete.days}")
    private int passedOldReports;

    @Value("${reports.failed.delete.days}")
    private int failedOldReports;

    private static String decodeName(String name) throws Exception {
        if (name == null || name.length() == 0) {
            return "unknown";
        }
        String ret = java.net.URLDecoder.decode(name, StandardCharsets.UTF_8);

        // also check for a few other things in the string:
        ret = ret.replaceAll("=\\?utf-8\\?q\\?", "");
        ret = ret.replaceAll("\\?=", "");
        ret = ret.replaceAll("=20", " ");

        return ret;
    }

    /*@Scheduled(cron = "0 45 0 * * ?")
    //@Scheduled(fixedRate = 10000)
    public void deleteUploadDirectoryTask() throws IOException, URISyntaxException {
        String downloadDir = HotFolderConstants.DEFAULT_HOT_FOLDER_LOCATION;
        List<File> files = Files.list(Path.of(downloadDir))
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toList());
        for (File file : files) {
            fileService.deleteFile(file);
        }
    }*/

    /*@Scheduled(cron = "0 20 0 * * ?")
    //@Scheduled(fixedRate = 10000)
    public void deleteOldQAReports() throws IOException, URISyntaxException {
        // Clean passed reports
        List<QATestResult> qaTestResultList = qaRepository.findAllByCreationTimeBeforeAndResultStatus(LocalDateTime.now().minusDays(passedOldReports), 1);
        deleteData(qaTestResultList);
        // Clean partial failed reports
        qaTestResultList = qaRepository.findAllByCreationTimeBeforeAndResultStatus(LocalDateTime.now().minusDays(failedOldReports), 2);
        deleteData(qaTestResultList);
        // Clean failed reports
        qaTestResultList = qaRepository.findAllByCreationTimeBeforeAndResultStatus(LocalDateTime.now().minusDays(failedOldReports), 3);
        deleteData(qaTestResultList);
    }*/

    /*private void deleteData(List<QATestResult> qaTestResultList) {
        qaTestResultList.forEach(qaTestResult -> {
            fileService.deleteFilesByQaResult(qaTestResult);
        });
    }*/

    @Scheduled(cron = "0 30 0 * * ?")
    //@Scheduled(fixedRate = 10000)
    public void deleteOldCronReport() throws IOException, URISyntaxException {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -Integer.parseInt(env.getProperty("reports.delete.days")));
            Date oldDate = cal.getTime();
            List<CronHistory> cronHistories = new ArrayList<>();
            for (CronHistory cronHistory : cronHistoryRepository.findAll()) {
                if (dateFormat.parse(cronHistory.getJobTime()).before(oldDate)) {
                    cronHistories.add(cronHistory);
                }
            }
            cronHistoryRepository.deleteAll(cronHistories);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
}