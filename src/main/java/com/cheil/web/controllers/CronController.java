package com.cheil.web.controllers;

import com.cheil.core.HotFolderConstants;
import com.cheil.core.mongo.model.CronHistory;
import com.cheil.core.mongo.repository.CronHistoryRepository;
import com.cheil.core.mongotemplate.QATestResult;
import com.cheil.core.mongotemplate.repository.QARepository;
import com.cheil.core.service.FileService;
import com.cheil.web.controllers.admin.category.CategoryController;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.search.FlagTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/")
public class CronController extends BaseController {

    Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    private Environment env;

    @Autowired
    private QARepository qaRepository;

    @Autowired
    private CronHistoryRepository cronHistoryRepository;

    @Autowired
    private FileService fileService;

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

    private static int saveFile(File saveFile, Part part) throws Exception {

        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(saveFile));

        byte[] buff = new byte[2048];
        InputStream is = part.getInputStream();
        int ret = 0, count = 0;
        while ((ret = is.read(buff)) > 0) {
            bos.write(buff, 0, ret);
            count += ret;
        }
        bos.close();
        is.close();
        return count;
    }

    @Scheduled(cron = "0 45 0 * * ?")
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
    }

    @Scheduled(cron = "0 20 0 * * ?")
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
    }

    private void deleteData(List<QATestResult> qaTestResultList) {
        qaTestResultList.forEach(qaTestResult -> {
            fileService.deleteFilesByQaResult(qaTestResult);
        });
    }

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

    //@Scheduled(cron = "0 * * * * ?")
    public void downloadEmailAttachments() throws Exception {

        String downloadDir = HotFolderConstants.DEFAULT_HOT_FOLDER_LOCATION;
        String host = env.getProperty("javax.mail.host");
        String user = env.getProperty("javax.mail.username");
        String pass = env.getProperty("javax.mail.password");

        // Create empty properties
        Properties props = new Properties();

        // Get the session
        Session session = Session.getInstance(props, null);

        // Get the store
        Store store = session.getStore("pop3");
        store.connect(host, user, pass);

        // Get folder
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        try {
            // Get directory listing
            Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.RECENT), false));
            System.out.println(messages);

            for (int i = 0; i < messages.length; i++) {

                Object content = messages[i].getContent();
                System.out.println("content" + content);

                if (content instanceof Multipart mp) {

                    for (int j = 0; j < mp.getCount(); j++) {

                        Part part = mp.getBodyPart(j);
                        String disposition = part.getDisposition();

                        if ((disposition != null) && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {

                            // Check if plain
                            MimeBodyPart mbp = (MimeBodyPart) part;
                            System.out.println("mime" + mbp);
                            if (!mbp.isMimeType("text/plain")) {
                                String attachmentName = decodeName(part.getFileName());
                                File savedir = new File(downloadDir);
                                savedir.mkdirs();
                                // File savefile = File.createTempFile( "emailattach", ".atch", savedir);
                                File saveFile = new File(downloadDir, attachmentName);
                                System.out.println(saveFile);
                                saveFile(saveFile, part);
                            }
                        }
                    } // end of multipart for loop
                } // end messages for loop

                // Finally delete the message from the server.
                messages[i].setFlag(Flags.Flag.DELETED, true);
            }

            // Close connection
            folder.close(true); // true tells the mail server to expunge deleted messages
            store.close();

        } catch (Exception e) {
            folder.close(true); // true tells the mail server to expunge deleted
            store.close();
            throw e;
        }
    }
}