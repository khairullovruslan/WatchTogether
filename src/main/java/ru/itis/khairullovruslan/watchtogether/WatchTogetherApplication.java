package ru.itis.khairullovruslan.watchtogether;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.itis.khairullovruslan.watchtogether.client.WatchTogetherClient;
import ru.itis.khairullovruslan.watchtogether.constants.FxmlConstants;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.io.IOException;

public class WatchTogetherApplication extends Application {

    public static WatchTogetherClient client;


    private static Stage stage;


    @Override
    public void start(Stage primaryStage) throws IOException {


        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(WatchTogetherApplication.class.getResource(FxmlConstants.WELCOME_SCREEN), LocalizationUtil.getBundle());
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Watch Together");
        primaryStage.setFullScreen(true);

        primaryStage.show();

    }


    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        WatchTogetherApplication.stage = stage;
    }

    public static WatchTogetherClient getClient() {
        return client;
    }

    public static void setClient(WatchTogetherClient client) {
        WatchTogetherApplication.client = client;
    }

}