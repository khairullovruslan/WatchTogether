package ru.itis.khairullovruslan.watchtogether.util.setting;

import ru.itis.khairullovruslan.watchtogether.constants.GameSettingConstants;

import java.util.Locale;
import java.util.ResourceBundle;



public class LocalizationUtil {
    private static Locale currentLocale = Locale.ENGLISH;



    private static ResourceBundle bundle = ResourceBundle.getBundle(GameSettingConstants.LOCALIZED_TEXTS_RESOURCE_BUNDLE, currentLocale);

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle(GameSettingConstants.LOCALIZED_TEXTS_RESOURCE_BUNDLE, currentLocale);
    }
    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

}