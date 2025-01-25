package ru.itis.khairullovruslan.watchtogether.controllers.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import ru.itis.khairullovruslan.watchtogether.constants.FxmlConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.base.Controller;
import ru.itis.khairullovruslan.watchtogether.controllers.base.MenuController;
import ru.itis.khairullovruslan.watchtogether.controllers.controller.CreateRoomController;
import ru.itis.khairullovruslan.watchtogether.util.UIHelper;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.itis.khairullovruslan.watchtogether.constants.BundleConstants.ROOM_LINK_LABEL;
import static ru.itis.khairullovruslan.watchtogether.constants.BundleConstants.START_LABEL;

public class CreateRoomView extends MenuController implements Controller {

    @FXML
    private Label videoLinkLabel;
    @FXML
    private TextField videoLinkField;
    @FXML
    private Label createRoomLabel;

    private final CreateRoomController controller;


    public CreateRoomView() {
        this.controller = new CreateRoomController(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initSettingButton(FxmlConstants.CREATE_ROOM_SCREEN);

        initReturnToPreviousSceneLabel(FxmlConstants.PRE_WATCH_SETUP_SCREEN);
        setUiStyle();
        controller.setMouseClickedEvents();
    }

    private void setUiStyle() {
        Font virgilFont = UIHelper.getFont(40);

        customizeLabel(videoLinkLabel, ROOM_LINK_LABEL, virgilFont);
        customizeLabel(createRoomLabel, START_LABEL, virgilFont);

        videoLinkField.setFont(UIHelper.getFont(20));

    }

    private void customizeLabel(Label label, String localizationKey, Font font) {
        label.setText(LocalizationUtil.getBundle().getString(localizationKey));
        label.setFont(font);
        label.setTextFill(Paint.valueOf("black"));
    }


    public TextField getVideoLinkField() {
        return videoLinkField;
    }


    public Label getCreateRoomLabel() {
        return createRoomLabel;
    }


    public CreateRoomController getController() {
        return controller;
    }
}
