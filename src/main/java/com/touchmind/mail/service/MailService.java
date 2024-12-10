package com.touchmind.mail.service;

import com.touchmind.core.HotFolderConstants;
import com.touchmind.core.mongo.dto.ReportDto;
import com.touchmind.core.mongo.model.CronJob;
import com.touchmind.core.mongo.repository.CronJobProfileRepository;
import com.touchmind.core.mongo.repository.CronRepository;
import com.touchmind.core.mongo.repository.QaTestPlanRepository;
import com.touchmind.core.mongotemplate.QATestResult;
import com.touchmind.core.mongotemplate.repository.QARepository;
import com.touchmind.core.service.ExcelFileService;
import com.touchmind.qa.utils.TestDataUtils;
import com.opencsv.CSVWriter;
import freemarker.template.Configuration;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Service
public class MailService {

    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);
    @Autowired
    Configuration fmConfiguration;
    @Autowired
    Environment env;
    Logger logger = LoggerFactory.getLogger(MailService.class);
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private QARepository qaRepository;
    @Autowired
    private CronJobProfileRepository cronJobProfileRepository;
    @Autowired
    private QaTestPlanRepository qaTestPlanRepository;
    @Autowired
    private CronRepository cronRepository;
    @Autowired
    private ExcelFileService excelFileService;

    private Properties getServerProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", env.getProperty("spring.transport.protocol"));
        properties.setProperty("mail.smtp.auth", env.getProperty("spring.smtp.auth"));
        properties.setProperty("mail.smtp.starttls.enable", env.getProperty("spring.smtp.starttls.enable"));
        properties.setProperty("mail.debug", env.getProperty("spring.mail.debug"));
        properties.setProperty("mail.smtp.ssl.enable", env.getProperty("spring.smtp.ssl.enable"));
        return properties;
    }

    public void sendCheilEmail(EMail mail) {
        try {
            MimeMessage mimeMessage = new MimeMessage(getSession());
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setFrom(env.getProperty("javax.mail.username"));
            File attachment = mail.getAttachment();
            if (attachment != null) {
                mimeMessageHelper.addAttachment(attachment.getName(), attachment);
            }
            //mimeMessageHelper.setTo(mail.getTo());
            if (mail.getTo().contains(",")) {
                for (String mailTo : mail.getTo().split(",")) {
                    mimeMessageHelper.addTo(mailTo);
                }
            } else {
                mimeMessageHelper.addTo(mail.getTo());
            }
            //mail.setContent(geContentFromTemplate(mail.getModel()));
            //mail.setContent("Please, find the details in the attachment");
            mimeMessageHelper.setText(mail.getContent(), true);
            logger.info("Sending mail to " + mail.getTo());
            Transport.send(mimeMessageHelper.getMimeMessage());
            logger.info("Sent mail to " + mail.getTo());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    private JavaMailSenderImpl getMailSender() {
        mailSender.setHost(env.getProperty("javax.mail.host"));
        mailSender.setPort(Integer.valueOf(env.getProperty("javax.mail.port")));
        mailSender.setUsername(env.getProperty("javax.mail.username"));
        mailSender.setPassword(env.getProperty("javax.mail.password"));
        Properties props = mailSender.getJavaMailProperties();
        props.putAll(getServerProperties());
        return mailSender;
    }

    public void sendEmail(EMail mail) {
        MimeMessage mimeMessage = getMailSender().createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setFrom(env.getProperty("javax.mail.username"));
            //mimeMessageHelper.setTo(mail.getTo());
            for (String mailTo : mail.getTo().split(",")) {
                mimeMessageHelper.addTo(mailTo);
            }
            if (StringUtils.isEmpty(mail.getContent())) {
                mail.setContent(geContentFromTemplate("email-template.flth", mail.getModel()));
            }
            mimeMessageHelper.setText(mail.getContent(), true);

            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public String geContentFromTemplate(String templateName, Map<String, Object> model) {
        StringBuffer content = new StringBuffer();

        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate(templateName), model));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private Session getSession() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", env.getProperty("javax.mail.protocol"));
        props.setProperty("mail.host", env.getProperty("javax.mail.host"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", env.getProperty("javax.mail.port"));
        props.put("mail.smtp.socketFactory.port", env.getProperty("javax.mail.port"));
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        return Session.getDefaultInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(env.getProperty("javax.mail.username"), env.getProperty("javax.mail.password"));
                    }
                });
    }

    public void sendMail(Map<String, String> data) {
        if (StringUtils.isNotEmpty(data.get("emails"))) {
            EMail eMail = getMailInstance(data);
            String subject = data.get(TestDataUtils.Field.EMAIL_SUBJECT.toString());
            eMail.setSubject(data.get(TestDataUtils.Field.JOB_TIME.toString()) + " - " + subject);
            String sessionId = data.get(TestDataUtils.Field.SESSION_ID.toString());
            eMail.setContent(getMailContent("qa-report.html", data, sessionId, eMail));
            sendCheilEmail(eMail);
        }
    }

    public void sendMailBulkResults(String subject, String emails, List<QATestResult> qaTestResults) {
        if (StringUtils.isNotEmpty(emails)) {
            EMail eMail = getMailInstance(emails);
            eMail.setSubject(subject);
            eMail.setContent(getMailContentForBulkMail("qa-report.html", qaTestResults, eMail));
            sendCheilEmail(eMail);
        }
    }

    public void sendMailForSub(Map<String, String> data, String toEmail, String testPlan) {
        if (StringUtils.isNotEmpty(toEmail)) {
            EMail eMail = new EMail();
            eMail.setTo(toEmail);
            String subject = data.get(TestDataUtils.Field.EMAIL_SUBJECT.toString());
            eMail.setSubject(subject);
            String sessionId = data.get(TestDataUtils.Field.SESSION_ID.toString());
            if (StringUtils.isNotEmpty(testPlan)) {
                String testPlanIdentifier = qaTestPlanRepository.findByRecordId(testPlan).getIdentifier();
                eMail.setContent(getMailContentForSub("qa-report.html", data, sessionId, testPlanIdentifier, eMail));
            }
            sendCheilEmail(eMail);
        }
    }

    private EMail getMailInstance(Map<String, String> data) {
        if (StringUtils.isNotEmpty(data.get("emails"))) {
            EMail eMail = new EMail();
            eMail.setTo(data.get(TestDataUtils.Field.EMAILS.toString()));
            eMail.setFrom(env.getProperty("javax.mail.username"));
            return eMail;
        }
        return null;
    }

    private EMail getMailInstance(String emails) {
        if (StringUtils.isNotEmpty(emails)) {
            EMail eMail = new EMail();
            eMail.setTo(emails);
            eMail.setFrom(env.getProperty("javax.mail.username"));
            return eMail;
        }
        return null;
    }

    public String getMailContentForBulkMail(String template, List<QATestResult> qaTestResult, EMail eMail) {
        Map<String, Object> context = new HashMap<>();
        String serverUrl = env.getProperty("server.url");
        context.put(TestDataUtils.Field.SERVER_URL.toString(), serverUrl);
        context.put("qaTestResults", qaTestResult);
        eMail.setAttachment(createAttachment(qaTestResult));
        populateCampaign(qaTestResult, context);
        return geContentFromTemplate(template, context);
    }

    public String getMailContent(String template, Map<String, String> data, String sessionId, EMail eMail) {
        Map<String, Object> context = new HashMap<>();
        data.forEach((key, value) -> {
            context.put(key, value);
        });
        String serverUrl = env.getProperty("server.url");
        List<QATestResult> qaTestResult = qaRepository.findBySessionId(sessionId);
        eMail.setAttachment(createAttachment(qaTestResult));
        populateCampaign(qaTestResult, context);
        context.put(TestDataUtils.Field.SERVER_URL.toString(), serverUrl);
        context.put("qaTestResults", qaTestResult);
        return geContentFromTemplate(template, context);
    }

    private File createAttachment(List<QATestResult> qaTestResult) {
        String currentTimeInMills = String.valueOf(System.currentTimeMillis());
        String fileName = currentTimeInMills + "_failedSKUs.xlsx";
        Path path = Paths.get(HotFolderConstants.DEFAULT_HOT_FOLDER_LOCATION + "/" + fileName);
        File file = path.toFile();
        List<MultiValuedMap<String, String>> failedErrorMap = new ArrayList<>();
        for (QATestResult result : qaTestResult) {
            MultiValuedMap<String, String> errorMap = new ArrayListValuedHashMap<>();
            if (result.getFailedSkusError() != null) {
                Map<String, String> skuErrorMap = result.getFailedSkusError();
                for (String keySet : skuErrorMap.keySet()) {
                    errorMap.put(keySet, skuErrorMap.get(keySet));
                }
                failedErrorMap.add(errorMap);
            }
        }
        try {
            if (CollectionUtils.isNotEmpty(failedErrorMap)) {
                excelFileService.writeDataToExcel(file, failedErrorMap);
            }
        } catch (IOException e) {
            LOG.error("Exception while creating attachment ", e);
        }
        return file;
    }

    private void populateCampaign(List<QATestResult> qaTestResult, Map<String, Object> context) {
        if (CollectionUtils.isNotEmpty(qaTestResult)) {
            String cronId = qaTestResult.get(0).getUser();
            CronJob cronJob = cronRepository.findByIdentifier(cronId);
            if (cronJob != null) {
                context.put("campaign", cronJob.getCampaign());
            }
        }
    }

    public String getMailContentForSub(String template, Map<String, String> data, String sessionId, String testPlan, EMail eMail) {
        Map<String, Object> context = new HashMap<>();
        data.forEach((key, value) -> {
            context.put(key, value);
        });
        String serverUrl = env.getProperty("server.url");
        String subsidiary = data.get("subsidiary");
        List<QATestResult> qaTestResult = qaRepository.findBySessionIdAndSubsidiaryAndTestName(sessionId, subsidiary, testPlan);
        eMail.setAttachment(createAttachment(qaTestResult));
        context.put(TestDataUtils.Field.SERVER_URL.toString(), serverUrl);
        context.put("qaTestResults", qaTestResult);
        populateCampaign(qaTestResult, context);
        return geContentFromTemplate(template, context);
    }

    public void sendToolkitEmail(ReportDto reportDto, List<Map<String, Object>> data, List<String> headers, List<String> params, Map<String, String> pivotHeaders) {
        String[] subjectArray = reportDto.getNodePath().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        StringBuilder builder = new StringBuilder();
        for (String subject : subjectArray) {
            builder.append(subject);
            builder.append("_");
        }
        String reportName = builder.toString().replaceAll("_$", "").replaceAll(StringUtils.SPACE, "");
        EMail eMail = new EMail();
        Map<String, Integer> pivotData = new HashMap<>();
        eMail.setAttachment(createAttachment(data, headers, params, reportName, pivotData, pivotHeaders));
        eMail.setTo(reportDto.getEmail());
        eMail.setFrom(env.getProperty("javax.mail.username"));
        eMail.setSubject(df.format(new Date()) + " - " + reportName.replaceAll("_", StringUtils.SPACE) + "!");
        Map<String, Object> context = new HashMap<>();
        context.put("toolkitResult", pivotData);
        eMail.setContent(geContentFromTemplate("toolkit-report.html", context));
        sendCheilEmail(eMail);
    }

    private File createAttachment(List<Map<String, Object>> data, List<String> headers, List<String> params, String fileName, Map<String, Integer> pivotData, Map<String, String> pivotHeaders) {
        File file = new File(fileName + ".csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeNext(headers.toArray(new String[0]));

            for (Map<String, Object> mapData : data) {
                List<String> rowData = new ArrayList<>();
                for (String param : params) {
                    String value = String.valueOf(mapData.get(param));
                    String result = value.contains("|") ? value.split("\\|")[0] : value;
                    rowData.add(result);
                    if (pivotHeaders.containsKey(param)) {
                        if (StringUtils.isNotEmpty(result) && pivotData.containsKey(result)) {
                            pivotData.put(result, pivotData.get(result) != null ? pivotData.get(result) + 1 : 0);
                        } else {
                            pivotData.put(result, 1);
                        }
                    }
                }
                writer.writeNext(rowData.toArray(new String[0]));
            }
            // closing writer connection
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
