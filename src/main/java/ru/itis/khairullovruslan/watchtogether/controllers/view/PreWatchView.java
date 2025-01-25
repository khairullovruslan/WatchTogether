package ru.itis.khairullovruslan.watchtogether.controllers.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import ru.itis.khairullovruslan.watchtogether.constants.FxmlConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.base.Controller;
import ru.itis.khairullovruslan.watchtogether.controllers.base.MenuController;
import ru.itis.khairullovruslan.watchtogether.controllers.controller.PreWatchController;
import ru.itis.khairullovruslan.watchtogether.util.UIHelper;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.itis.khairullovruslan.watchtogether.constants.BundleConstants.ROOM_CREATE_LABEL;
import static ru.itis.khairullovruslan.watchtogether.constants.BundleConstants.ROOM_JOIN_BY_CODE_LABEL;

public class PreWatchView extends MenuController implements Controller {

    @FXML
    private Label createRoomLabel;

    @FXML
    private Label addedByCodeLabel;

    private final PreWatchController preWatchController;

    public PreWatchView() {
        this.preWatchController = new PreWatchController(this);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setStyle();
        initSettingButton(FxmlConstants.PRE_WATCH_SETUP_SCREEN);
        preWatchController.setMouseClickedEvents();
        initReturnToPreviousSceneLabel(FxmlConstants.WELCOME_SCREEN);

    }

    private void setStyle() {
        Font virgilFont = UIHelper.getFont(100);

        createRoomLabel.setText(LocalizationUtil.getBundle().getString(ROOM_CREATE_LABEL));
        addedByCodeLabel.setText(LocalizationUtil.getBundle().getString(ROOM_JOIN_BY_CODE_LABEL));

        createRoomLabel.setFont(virgilFont);
        addedByCodeLabel.setFont(virgilFont);

        addedByCodeLabel.setTextFill(Paint.valueOf("black"));
        createRoomLabel.setTextFill(Paint.valueOf("black"));

    }

    public Label getCreateRoomLabel() {
        return createRoomLabel;
    }

    public Label getAddedByCodeLabel() {
        return addedByCodeLabel;
    }
}
