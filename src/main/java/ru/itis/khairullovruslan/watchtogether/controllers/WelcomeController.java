package ru.itis.khairullovruslan.watchtogether.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import ru.itis.khairullovruslan.watchtogether.constants.FxmlConstants;
import ru.itis.khairullovruslan.watchtogether.util.ScreenVisualizer;
import ru.itis.khairullovruslan.watchtogether.util.UIHelper;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {

    @FXML
    private Label startGameLabel;

    @FXML
    private Label welcomeLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Font virgilFont = UIHelper.getFont(120);
        welcomeLabel.setFont(virgilFont);
        welcomeLabel.setTextFill(Paint.valueOf("black"));
        startGameLabel.setOnMouseClicked(event ->
                ScreenVisualizer.show(FxmlConstants.PRE_WATCH_SETUP_SCREEN));

    }
}

