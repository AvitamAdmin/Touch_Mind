package com.cheil.mail;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CheilMailService extends DefaultEmailService {

    public String getRegistrationDeepLink(Date mailsAfter, boolean isNewMessages) throws EmailNotRecievedException {
        /*Store store = getMailStore();
        if(store == null) {
            return null;
        }
        Folder folder = getFolder(store,env.getProperty("jakarta.mail.folder"));
        if(folder == null) {
            return null;
        }

        Message[] messages = isNewMessages?getNewMessages(folder):getAllMessages(folder);
        String deepLink = null;
        for (Message message:messages) {
            MimeMessageParser parser =getParser((MimeMessage)message);
            if(parser != null) {
                String emailRecivedDate = null;
                try {
                    emailRecivedDate = message.getSentDate().toString();
                } catch (MessagingException e) {
                    throw new EmailNotRecievedException();
                }
                if(isNewEmail(emailRecivedDate,mailsAfter)) {
                    deepLink = MailUtil.getDeepLink(parser.getHtmlContent(),env.getProperty("samsung.mail.subject"),env.getProperty("samsung.mail.body.search.term"));
                }
            }
        }
        if(CheilStringUtils.isEmpty(deepLink)) {
            throw  new EmailNotRecievedException();
        }*/
        return "";
    }

    private boolean isNewEmail(String emailRecievedDate, Date currentDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        try {
            return currentDate.after(formatter.parse(emailRecievedDate));
        } catch (ParseException e) {
            LOG.error("Could not parse date " + e.getMessage());
        }
        return false;
    }

}
