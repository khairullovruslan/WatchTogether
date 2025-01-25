package ru.itis.khairullovruslan.watchtogether.controllers.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.paint.Paint;
import ru.itis.khairullovruslan.watchtogether.controllers.controller.SettingController;
import ru.itis.khairullovruslan.watchtogether.controllers.base.Controller;
import ru.itis.khairullovruslan.watchtogether.controllers.base.MenuController;
import ru.itis.khairullovruslan.watchtogether.util.UIHelper;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingView extends MenuController implements Controller{


    @FXML
    private Label welcomeLabel;
    @FXML
    private RadioButton englishRadioButton;

    @FXML
    private RadioButton russianRadioButton;

    private final SettingController settingController;

    public SettingView() {
        this.settingController = new SettingController(this);
    }

    public void setPrevSceneFxml(String prevSceneFxml) {
        initReturnToPreviousSceneLabel(prevSceneFxml);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        settingController.updateTextAfterChangeLanguage();
        setStyle();
        settingController.initializeLanguageSelection();
    }

    private void setStyle() {
        welcomeLabel.setFont(UIHelper.getFont(70));
        welcomeLabel.setTextFill(Paint.valueOf("black"));

        englishRadioButton.setFont(UIHelper.getFont(50));
        russianRadioButton.setFont(UIHelper.getFont(50));

        englishRadioButton.getStyleClass().add("radio-button");
        russianRadioButton.getStyleClass().add("radio-button");
    }


    public Label getWelcomeLabel() {
        return welcomeLabel;
    }
    public Label getBackLabel() {
        return backLabel;
    }

    public RadioButton getEnglishRadioButton() {
        return englishRadioButton;
    }

    public RadioButton getRussianRadioButton() {
        return russianRadioButton;
    }
}
