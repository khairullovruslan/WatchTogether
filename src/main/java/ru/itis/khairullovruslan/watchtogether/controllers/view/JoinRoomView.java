package ru.itis.khairullovruslan.watchtogether.controllers.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import ru.itis.khairullovruslan.watchtogether.constants.FxmlConstants;
import ru.itis.khairullovruslan.watchtogether.constants.StyleConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.base.Controller;
import ru.itis.khairullovruslan.watchtogether.controllers.base.MenuController;
import ru.itis.khairullovruslan.watchtogether.controllers.controller.JoinRoomController;
import ru.itis.khairullovruslan.watchtogether.util.UIHelper;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.itis.khairullovruslan.watchtogether.constants.BundleConstants.*;

public class JoinRoomView extends MenuController implements Controller {
    @FXML
    private Label enterCodeLabel;
    @FXML
    private Label enterPasswordLabel;
    @FXML
    private Label enterNameLabel;
    @FXML
    private Label joinRoomLabel;
    @FXML
    private Label errorLabel;

    @FXML
    private TextField codeField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField passwordField;

    @FXML
    private ImageView insertView;



    private final JoinRoomController controller;

    public JoinRoomView() {
        this.controller = new JoinRoomController(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initSettingButton(FxmlConstants.JOIN_THE_ROOM_SCREEN);
        initReturnToPreviousSceneLabel(FxmlConstants.PRE_WATCH_SETUP_SCREEN);
        setUiStyle();
        controller.setMouseClickedEvents();
    }

    private void setUiStyle() {
        Font virgilFont = UIHelper.getFont(50);
        insertView.setImage(UIHelper.getImage(StyleConstants.INSERT_PNG));
        insertView.setTranslateX(15);
        insertView.setTranslateY(5);
        configureLabel(enterCodeLabel, ROOM_ENTER_CODE, virgilFont, Paint.valueOf("black"));

        configureLabel(enterNameLabel, ROOM_ENTER_NAME, virgilFont, Paint.valueOf("black"));

        configureLabel(enterPasswordLabel, ROOM_ENTER_PASSWORD, virgilFont, Paint.valueOf("black"));

        configureLabel(joinRoomLabel, ROOM_JOIN, UIHelper.getFont(42), Paint.valueOf("#970000"));
        configureLabel(errorLabel, UIHelper.getFont(42), Paint.valueOf("#970000"));
    }

    private void configureLabel(Label label, String localizationKey, Font font, Paint color) {
        label.setText(LocalizationUtil.getBundle().getString(localizationKey));
        label.setFont(font);
        label.setTextFill(color);
    }
    private void configureLabel(Label label,  Font font, Paint color) {
        label.setFont(font);
        label.setTextFill(color);
    }


    public TextField getCodeField() {
        return codeField;
    }


    public TextField getNameField() {
        return nameField;
    }

    public Label getJoinRoomLabel() {
        return joinRoomLabel;
    }


    public TextField getPasswordField() {
        return passwordField;
    }


    public Label getErrorLabel() {
        return errorLabel;
    }


    public ImageView getInsertView() {
        return insertView;
    }
}
