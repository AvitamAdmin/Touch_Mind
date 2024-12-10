package com.touchmind;

import java.io.IOException;
import java.util.Locale;

public class CountryCodeGenerator {
    /**
     * This is the code used to generate the enum content
     */
    public static void main(String[] args) throws IOException {

        createCCodes();
    }

    public static void createCCodes() throws IOException {

        Locale[] locales = Locale.getAvailableLocales();
        int counter = 0;
        for (Locale locale : locales) {
            counter++;
            String country = null, language = null;
            try {
                country = locale.getISO3Country();
                if (locale.getCountry() != "") {
                    //1,"de_AT","Austria");
                    System.out.println("INSERT INTO iso_code (id,iso_code,country) VALUES ( " + counter + ",'" + country + "','" + locale.getDisplayCountry() + "');");
                }
                language = locale.getLanguage().toUpperCase();
            } catch (Exception e) {
                continue;
            }

            if (!"".equals(country) && !"".equals(language)) {
                String ccode = country + "," + country + "-" + language + "\n";
            }
        }
    }
}
