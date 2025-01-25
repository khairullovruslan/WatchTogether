package ru.itis.khairullovruslan.watchtogether.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import ru.itis.khairullovruslan.watchtogether.WatchTogetherApplication;
import ru.itis.khairullovruslan.watchtogether.constants.FxmlConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.view.SettingView;
import ru.itis.khairullovruslan.watchtogether.controllers.view.VideoPlayerView;

import java.io.IOException;
import java.util.Objects;

public class ScreenVisualizer {
    public static void show(String fxmlName) {
        try {
            Stage stage = WatchTogetherApplication.getStage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(ScreenVisualizer.class.getResource(fxmlName)));
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showSettingScene(String prevSceneFxml) {
        try {
            Stage stage = WatchTogetherApplication.getStage();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ScreenVisualizer.class.getResource(FxmlConstants.SETTING_SCREEN)));
            Parent root = loader.load();
            root.getStylesheets().add(Objects.requireNonNull(ScreenVisualizer.class.getResource("/style/radio.css")).toExternalForm());

            SettingView controller = loader.getController();
            controller.setPrevSceneFxml(prevSceneFxml);

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showVideo(String path) {
        try {
            Stage stage = WatchTogetherApplication.getStage();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ScreenVisualizer.class.getResource(FxmlConstants.VIDEO_PLAYER_SCREEN)));
            Parent root = loader.load();


            root.getStylesheets().add(Objects.requireNonNull(ScreenVisualizer.class.getResource("/style/slider.css")).toExternalForm());

            VideoPlayerView VideoPlayerView = loader.getController();
            VideoPlayerView.setSceneWidth(stage.getScene().getWidth());
            VideoPlayerView.setSceneHeight(stage.getScene().getHeight());
            VideoPlayerView.setMedia(path);
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





}
