package ru.itis.khairullovruslan.watchtogether.controllers.controller;

import javafx.scene.control.ToggleGroup;
import ru.itis.khairullovruslan.watchtogether.constants.GameSettingConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.view.SettingView;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.util.Locale;

import static ru.itis.khairullovruslan.watchtogether.constants.BundleConstants.BACK_LABEL;
import static ru.itis.khairullovruslan.watchtogether.constants.BundleConstants.SETTING_WELCOME_LABEL;

public class SettingController {
    private final SettingView settingViewController;

    public SettingController(SettingView settingViewController) {
        this.settingViewController = settingViewController;
    }

    public void initializeLanguageSelection() {
        ToggleGroup languageToggleGroup = createLanguageToggleGroup();
        configureLanguageSelection(languageToggleGroup);
        setInitialLanguageSelection(languageToggleGroup);
    }


    public void updateTextAfterChangeLanguage() {
        settingViewController.getWelcomeLabel().setText(LocalizationUtil.getBundle().getString(SETTING_WELCOME_LABEL));
        settingViewController.getBackLabel().setText(LocalizationUtil.getBundle().getString(BACK_LABEL));
    }

    private ToggleGroup createLanguageToggleGroup() {
        ToggleGroup toggleGroup = new ToggleGroup();
        settingViewController.getEnglishRadioButton().setToggleGroup(toggleGroup);
        settingViewController.getRussianRadioButton().setToggleGroup(toggleGroup);
        return toggleGroup;
    }

    private void configureLanguageSelection(ToggleGroup languageToggleGroup) {
        languageToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == settingViewController.getEnglishRadioButton()) {
                setLocale(GameSettingConstants.ENGLISH_LOCALIZATION);
            } else if (newValue == settingViewController.getRussianRadioButton()) {
                setLocale(GameSettingConstants.RUSSIAN_LOCALIZATION);
            }
            updateTextAfterChangeLanguage();
        });
    }

    private void setInitialLanguageSelection(ToggleGroup languageToggleGroup) {
        String currentLanguage = LocalizationUtil.getCurrentLocale().getLanguage();
        if (currentLanguage.equals(GameSettingConstants.RUSSIAN_LOCALIZATION)) {
            settingViewController.getRussianRadioButton().setSelected(true);
        } else {
            settingViewController.getEnglishRadioButton().setSelected(true);
        }
    }

    private void setLocale(String localeCode) {
        LocalizationUtil.setLocale(new Locale(localeCode));
    }


}
