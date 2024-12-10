package com.cheil.mail;

public class MailUtil {

    private final static String SUBJECT = "Willkommen zum corporatebenefits Samsung Partnership Programm!";
    private final static String DEEP_LINK = "https://shop.samsung.com/de/multistore/deepp/corporatebenefits/registration/verifyEmail";


    public static String getDeepLink(String readHtmlContent, String subject, String searchTerm) {
        String[] lines = readHtmlContent.split("\r?\n|\r");
        String deepLink = null;

        for (String line : lines) {
            if (line.contains(searchTerm) && SUBJECT.equals(subject)) {
                String[] tokens = line.split("\"");
                for (String link : tokens) {
                    if (link.contains(searchTerm)) {
                        deepLink = link;
                    }
                }
            }
        }
        return deepLink;
    }
}
