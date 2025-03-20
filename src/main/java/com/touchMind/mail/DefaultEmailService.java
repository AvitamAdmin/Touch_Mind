package com.touchMind.mail;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class DefaultEmailService {

    Logger LOG = LoggerFactory.getLogger(DefaultEmailService.class);

    @Autowired
    Environment env;

    public Store getMailStore() {
        Session session = Session.getDefaultInstance(getServerProperties());
        Store store = null;
        try {
            store = session.getStore(env.getProperty("jakarta.mail.protocol"));
            store.connect(env.getProperty("jakarta.mail.username"), env.getProperty("jakarta.mail.password"));
            return store;
        } catch (NoSuchProviderException e) {
            LOG.error("Invalid protocol " + env.getProperty("javax.mail.protocol") + " please make sure protocol is valid example imap , pop3 ");
            LOG.error(e.getMessage());
        } catch (MessagingException e) {
            LOG.error("Unable to login to mail store with user " + env.getProperty("javax.mail.username") + " please make sure username and password is correctly configured");
            LOG.error("Example jakarta.mail.username=xxxx jakarta.mail.password=xxxx ");
            LOG.error(e.getMessage());
        }

        return null;
    }

    public Folder getFolder(Store store, String folder) {
        Folder folderInbox = null;
        try {
            folderInbox = store.getFolder(folder);
            folderInbox.open(Folder.READ_WRITE);
            return folderInbox;
        } catch (MessagingException e) {
            LOG.error("No folder names " + folder + "exist or not read able to read and write on store please make sure folder name is correct");
            LOG.error(e.getMessage());
        }
        return null;
    }

    public Message[] getNewMessages(Folder folder) {
        Flags seen = new Flags(Flags.Flag.SEEN);
        FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
        Message[] messages = new Message[0];
        try {
            messages = folder.search(unseenFlagTerm);
            folder.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
            return messages;
        } catch (MessagingException e) {
            LOG.error("Error occured reading messages from folder " + folder);
            LOG.error(e.getMessage());
        }
        return messages;
    }

    public Message[] getAllMessages(Folder folder) {
        try {
            return folder.getMessages();
        } catch (MessagingException e) {
            LOG.error("Error occured reading messages from folder " + folder);
            LOG.error(e.getMessage());
        }
        return null;
    }

    /**
     * Returns a Properties object which is configured for a POP3/IMAP server
     *
     * @return a Properties object
     */
    private Properties getServerProperties() {
        Properties properties = new Properties();
        properties.put(String.format("mail.%s.host", env.getProperty("javax.mail.protocol")), env.getProperty("javax.mail.host"));
        properties.put(String.format("mail.%s.port", env.getProperty("javax.mail.protocol")), env.getProperty("javax.mail.port"));
        properties.setProperty(String.format("mail.%s.socketFactory.class", env.getProperty("javax.mail.protocol")), "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", env.getProperty("javax.mail.protocol")), "false");
        properties.setProperty(String.format("mail.%s.socketFactory.port", env.getProperty("javax.mail.protocol")), env.getProperty("javax.mail.port"));
        return properties;
    }

    public void sendMail(Message message) throws MessagingException {
        Session session = Session.getDefaultInstance(getServerProperties());

        // Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("s.huple.touchMind@gmail.com"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
        message.setSubject("Mail Subject");

        String msg = "This is my first email using JavaMailer";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
