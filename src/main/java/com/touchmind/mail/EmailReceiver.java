package com.touchmind.mail;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;


public class EmailReceiver {

    private final static String SUBJECT = "Willkommen zum corporatebenefits Samsung Partnership Programm!";
    private final static String DEEP_LINK = "https://shop.samsung.com/de/multistore/deepp/corporatebenefits/registration/verifyEmail";

    /**
     * Test downloading e-mail messages
     */
    public static void main(String[] args) {
        // for POP3
        //String protocol = "pop3";
        //String host = "pop.gmail.com";
        //String port = "995";

        // for IMAP
        String protocol = "imap";
        String host = "imap.mail.eu-west-1.awsapps.com";
        //String host = "outlook.office365.com";
        String port = "993";


        String userName = "orderdata-import@cheil.de";
        //String userName = "s.huple@outlook.com";
        String password = "vV4KSxPM";
        //String password = "Welcome@2022!";
        EmailReceiver receiver = new EmailReceiver();
        receiver.downloadEmails(protocol, host, port, userName, password);
    }

    /**
     * Returns a Properties object which is configured for a POP3/IMAP server
     *
     * @param protocol either "imap" or "pop3"
     * @param host
     * @param port
     * @return a Properties object
     */
    private Properties getServerProperties(String protocol, String host,
                                           String port) {
        Properties properties = new Properties();


        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL setting
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol),
                String.valueOf(port));

        return properties;
    }

    /**
     * Downloads new messages and fetches details for each message.
     *
     * @param protocol
     * @param host
     * @param port
     * @param userName
     * @param password
     */
    public void downloadEmails(String protocol, String host, String port,
                               String userName, String password) {
        Properties properties = getServerProperties(protocol, host, port);
        Session session = Session.getDefaultInstance(properties);


        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(userName, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);



/*
            // search for all "unseen" messages
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            Message messages[] = folderInbox.search(unseenFlagTerm);

            folderInbox.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
*/
            // fetches new messages from server
            Message[] messages = folderInbox.getMessages();

            for (int i = 0; i < messages.length; i++) {
                Message msg = messages[i];
                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();
                String subject = msg.getSubject();
                String toList = parseAddresses(msg
                        .getRecipients(Message.RecipientType.TO));
                String ccList = parseAddresses(msg
                        .getRecipients(Message.RecipientType.CC));
                String sentDate = msg.getSentDate().toString();

                String contentType = msg.getContentType();
                String messageContent = "";
                MimeMultipart cont = (MimeMultipart) msg.getContent();
                //  System.out.println("###### getContentType ==> "+cont.getContentType());;

                //System.out.println("###### getBodyPart ==> "+readHtmlContent((MimeMessage)msg));
                MimeMessage mimeMessage = (MimeMessage) msg;

                getDeepLink(readHtmlContent(mimeMessage), subject, sentDate);
/*
                if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    try {
                        Object content = msg.getContent();
                        if (content != null) {
                            messageContent = content.toString();
                        }
                    } catch (Exception ex) {
                        messageContent = "[Error downloading content]";
                        ex.printStackTrace();
                    }
                }
*/
                // print out details of each message
//                System.out.println("Message #" + (i + 1) + ":");
                //               System.out.println("\t From: " + from);
                //              System.out.println("\t To: " + toList);
                //              System.out.println("\t CC: " + ccList);
                //              System.out.println("\t Subject: " + subject);
                //              System.out.println("\t Sent Date: " + sentDate);
                //              System.out.println("\t Message: " + messageContent);
            }

            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void getDeepLink(String readHtmlContent, String subject, String sentDate) throws ParseException {
        String[] lines = readHtmlContent.split("\r?\n|\r");
        SimpleDateFormat formatter1 = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

        System.out.println();
        for (String line : lines) {
            if (line.contains(DEEP_LINK) && SUBJECT.equals(subject)) {
                String[] tokens = line.split("\"");
                for (String deepLink : tokens) {
                    if (deepLink.contains(DEEP_LINK)) {
                        System.out.println(deepLink);
                    }
                }
                System.out.println(formatter1.parse(sentDate));
            }
        }
    }

    /**
     * Returns a list of addresses in String format separated by comma
     *
     * @param address an array of Address objects
     * @return a string represents a list of addresses
     */
    private String parseAddresses(Address[] address) {
        String listAddress = "";

        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }

        return listAddress;
    }

    String readHtmlContent(MimeMessage message) throws Exception {
        return message.toString();
    }


}
