package com.nilsson.util;

import java.util.Locale;
import java.util.ResourceBundle;

// https://docs.oracle.com/cd/E10926_01/doc/owb.101/b12155/oracle/owb/transformation/LanguageManager.html
// https://en.wikipedia.org/wiki/Internationalization_and_localization

public class LanguageManager {

    private static LanguageManager instance;
    private ResourceBundle resourceBundle;
    private Locale currentLocale;

    private LanguageManager() {
        setLocale(new Locale("sv"));
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.resourceBundle = ResourceBundle.getBundle("i18n/messages", locale);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    // Toggle language
    public void toggleLanguage() {
        if (currentLocale.getLanguage().equals("sv")) {
            setLocale(new Locale("en"));
        } else {
            setLocale(new Locale("sv"));
        }
    }

    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return "Key not found: " + key;
        }
    }
}