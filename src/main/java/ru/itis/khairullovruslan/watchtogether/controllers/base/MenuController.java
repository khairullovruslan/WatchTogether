package ru.itis.khairullovruslan.watchtogether.controllers.base;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import ru.itis.khairullovruslan.watchtogether.constants.StyleConstants;
import ru.itis.khairullovruslan.watchtogether.util.ScreenVisualizer;
import ru.itis.khairullovruslan.watchtogether.util.UIHelper;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.util.Objects;

public abstract class MenuController{
    @FXML
    private ImageView settingImageView;

    @FXML
    protected Label backLabel;


    protected void initSettingButton(String currentFxml) {
        Image backImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(StyleConstants.SETTING_PNG)));
        settingImageView.setImage(backImage);


        settingImageView.setStyle("-fx-cursor: hand;");

        settingImageView.setOnMouseEntered(event -> {
            settingImageView.setTranslateY(-10);
        });

        settingImageView.setOnMouseExited(event -> {
            settingImageView.setTranslateY(0);
        });

        settingImageView.setOnMouseClicked(event ->
                ScreenVisualizer.showSettingScene(currentFxml));
    }

    protected void initReturnToPreviousSceneLabel(String prevSceneFxml) {
        backLabel.setText(LocalizationUtil.getBundle().getString("back"));
        backLabel.setFont(UIHelper.getFont(70));
        backLabel.setTextFill(Paint.valueOf("black"));
        backLabel.setOnMouseClicked(mouseEvent -> ScreenVisualizer.show(prevSceneFxml));

        backLabel.setStyle("-fx-cursor: hand;");
        backLabel.setOnMouseEntered(event -> {
            backLabel.setTranslateX(10);
            backLabel.setTranslateY(-10);
        });

        backLabel.setOnMouseExited(event -> {
            backLabel.setTranslateX(0);
            backLabel.setTranslateY(0);
        });


    }
}
