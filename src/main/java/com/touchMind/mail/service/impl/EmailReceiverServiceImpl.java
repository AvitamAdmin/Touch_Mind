package com.touchMind.mail.service.impl;

import com.touchMind.core.HotFolderConstants;
import com.touchMind.core.mongo.dto.ReportDto;
import com.touchMind.core.service.EmailAttachmentReaderJobService;
import com.touchMind.mail.service.EmailReceiverService;
import com.touchMind.tookit.service.impl.BaseService;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeBodyPart;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailReceiverServiceImpl extends BaseService implements EmailReceiverService {

    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
    Logger LOG = LoggerFactory.getLogger(EmailReceiverServiceImpl.class);

    @Autowired
    Environment env;
    @Autowired
    private EmailAttachmentReaderJobService emailAttachmentReaderJobService;

    @Override
    public void processData(Map<String, String> data) {
        ReportDto reportDto = getReportDto(data);
        String jobTime = df.format(new Date());
       // saveCronHistory(data.get("sessionId"), reportDto.getSubsidiary().getIdentifier(), reportDto.getEmail(), 0, jobTime, "Running", null, data.get("currentSessionId"), StringUtils.EMPTY);

        String host = env.getProperty("javax.mail.receiver.host");
        String user = env.getProperty("javax.mail.username");
        String pass = env.getProperty("javax.mail.password");


        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", env.getProperty("javax.mail.receiver.port"));
        props.put("mail.smtp.timeout", 5000);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, null);

        try {
            Store store = session.getStore(env.getProperty("javax.mail.receiver.protocol"));
            store.connect(host, user, pass);

            Folder folderInbox = store.getFolder(env.getProperty("javax.mail.folder"));
            folderInbox.open(Folder.READ_WRITE);

            Message[] messages = folderInbox.getMessages();

            Arrays.stream(messages).forEach(message -> {
                try {
                    extractMail(message, data.get("sourceAddress"));
                } catch (MessagingException | IOException e) {
                  //  saveCronHistory(data.get("sessionId"), reportDto.getSubsidiary().getIdentifier(), reportDto.getEmail(), 0, jobTime, "Failed", e.toString(), data.get("currentSessionId"), StringUtils.EMPTY);
                    throw new RuntimeException(e);
                }
            });

            folderInbox.close(true);
            store.close();
           // saveCronHistory(data.get("sessionId"), reportDto.getSubsidiary().getIdentifier(), reportDto.getEmail(), 0, jobTime, "Completed", null, data.get("currentSessionId"), StringUtils.EMPTY);
        } catch (MessagingException e) {
           // saveCronHistory(data.get("sessionId"), reportDto.getSubsidiary().getIdentifier(), reportDto.getEmail(), 0, jobTime, "Failed", e.toString(), data.get("currentSessionId"), StringUtils.EMPTY);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopCronJob(String recordId) {
        emailAttachmentReaderJobService.stopCronJob(recordId);
    }

    private void extractMail(Message message, String sourceAddress) throws MessagingException, IOException {
        showMailContent(message);
        downloadAttachmentFiles(message, sourceAddress);

    }

    private void showMailContent(Message message) throws MessagingException {
        LOG.debug("From: {} to: {} | Subject: {}", message.getFrom(), message.getReplyTo(), message.getSubject());
    }

    private void downloadAttachmentFiles(Message message, String sourceAddress) throws MessagingException, IOException {
        Object content = message.getContent();
        String subject = message.getSubject();
        boolean containsAttachment = false;
        if (content instanceof Multipart multiPart) {
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) && StringUtils.isNotBlank(part.getFileName())) {
                    containsAttachment = true;
                    StringBuilder fileName = new StringBuilder(subject);
                    fileName.append("-").append(part.getFileName());
                    if (StringUtils.equals(fileName.substring(0, StringUtils.indexOf(fileName, ".")), sourceAddress)) {
                        part.saveFile(HotFolderConstants.DEFAULT_HOT_FOLDER_LOCATION + File.separator + fileName);
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }
            }
        }
        if (!containsAttachment) {
            message.setFlag(Flags.Flag.DELETED, true);
        }
    }


}
