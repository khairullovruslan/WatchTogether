package ru.itis.khairullovruslan.watchtogether.controllers.view;

import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import ru.itis.khairullovruslan.watchtogether.WatchTogetherApplication;
import ru.itis.khairullovruslan.watchtogether.client.WatchTogetherClient;
import ru.itis.khairullovruslan.watchtogether.constants.StyleConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.base.Controller;
import ru.itis.khairullovruslan.watchtogether.controllers.controller.video.VideoKeyHandler;
import ru.itis.khairullovruslan.watchtogether.controllers.controller.video.VideoPlayerController;
import ru.itis.khairullovruslan.watchtogether.controllers.controller.video.VideoVolumeController;
import ru.itis.khairullovruslan.watchtogether.util.UIHelper;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.itis.khairullovruslan.watchtogether.constants.BundleConstants.*;


public class VideoPlayerView implements Controller {
    @FXML
    private MediaView mediaView;

    @FXML
    private Label currentTimeLabel;

    @FXML
    private BorderPane borderMediaPane;

    @FXML
    private ImageView loadingGifView;
    @FXML
    private Slider slider;
    @FXML
    private Slider volumeSlider;

    @FXML
    private ImageView volumeView;
    @FXML
    private VBox videoControlHBox;

    @FXML
    private TextFlow chatTextFlow;

    @FXML
    private Label volumeLabel;


    @FXML
    private Label chatLabel;

    @FXML
    private ImageView exitView;

    @FXML
    private ImageView chatImageView;

    @FXML
    private ImageView fullScreenView;

    @FXML
    private ImageView playImageView;
    @FXML
    private ImageView shareImageView;
    @FXML
    private VBox participantsVBox;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private TextField chatInputField;

    @FXML
    private ComboBox<String> participantsComboBox;

    @FXML
    private Label participantsLabel;

    @FXML
    private Label videoTitleLabel;

    @FXML
    private Rectangle hitArea;

    private MediaPlayer mediaPlayer;
    private final Tooltip tooltip = new Tooltip();

    private final Timeline animation = new Timeline();


    private double sceneWidth;
    private double sceneHeight;

    private double scale;

    private final VideoPlayerController videoPlayerController;
    private final VideoKeyHandler videoKeyHandler;
    private final VideoVolumeController videoVolumeController;

    public VideoPlayerView() {
        this.videoVolumeController = new VideoVolumeController(this);
        this.videoPlayerController = new VideoPlayerController(this);
        this.videoKeyHandler = new VideoKeyHandler(this);
    }


    public void setMedia(String videoPath) {
        WatchTogetherClient client = WatchTogetherApplication.getClient();
        client.sendMessage("GET_CLIENTS:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(), "nothing"));
        Stage stage = WatchTogetherApplication.getStage();
        stage.getScene().getRoot().setStyle("-fx-background-color: %s;".formatted("black"));
        loadingGifView.setImage(UIHelper.getImage(StyleConstants.LOADING_GIF));
        Media media = new Media(videoPath);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setOnReady(() -> {
            loadingGifView.setVisible(false);
            setBaseStyle();
            videoPlayerController.addListenersToUserAction();
            videoPlayerController.setupVBoxVisibility();
            videoVolumeController.addListenersToUserAction();
        });


    }


    private void setBaseStyle() {
        double stageWidth = sceneWidth;
        double mediaViewWidth = mediaView.getBoundsInParent().getWidth();
        scale = (stageWidth - 60) / (mediaViewWidth);
        mediaView.setScaleX(scale);
        mediaView.setScaleY(scale);
        mediaView.setTranslateY(-15);

        volumeSlider.setMaxWidth(1);

        participantsLabel.setText(LocalizationUtil.getBundle().getString(PARTICIPANTS_LABEL));
        chatLabel.setText(LocalizationUtil.getBundle().getString(CHAT_LABEL));
        chatInputField.setPromptText(LocalizationUtil.getBundle().getString(ENTER_YOUR_MESSAGE));

        participantsVBox.setBackground(Background.fill(Paint.valueOf("#0e0d10")));
        participantsVBox.setVisible(false);
        participantsVBox.setTranslateY(-15);
        participantsVBox.setTranslateX(participantsVBox.getLayoutX());
        participantsVBox.setMaxHeight(mediaView.getBoundsInParent().getHeight() * 0.77);
        chatTextFlow.setMaxHeight(sceneHeight - 200);
        chatTextFlow.setStyle("-fx-background-color: #18171b");

        participantsComboBox.setStyle("-fx-background-color: #18171b; -fx-border-radius: 5;-fx-border-color: #bdc3c7; ");
        chatScrollPane.lookup(".thumb").setStyle("-fx-background-color: #000000; " +
                "-fx-background-radius: 5;");
        chatScrollPane.lookup(".scroll-bar:vertical").setStyle("-fx-background-color: #18171b; " +
                "-fx-pref-width: 10px;");

        WatchTogetherApplication.getStage().getScene().getRoot().setStyle("-fx-background-color: #201f23");


        shareImageView.setImage(UIHelper.getImage(StyleConstants.SHARE_PNG));
        shareImageView.setTranslateX(stageWidth - 600);


        chatImageView.setImage(UIHelper.getImage(StyleConstants.CHAT_PNG));
        chatImageView.setTranslateX(stageWidth - 600);



        fullScreenView.setImage(UIHelper.getImage(StyleConstants.FULLSCREEN_PNG));
        fullScreenView.setTranslateX(stageWidth - 600);

        playImageView.setImage(UIHelper.getImage(StyleConstants.PLAY_PNG));

        exitView.setImage(UIHelper.getImage(StyleConstants.EXIT_PNG));
        exitView.setTranslateY(-35);

        volumeView.setImage(UIHelper.getImage(StyleConstants.VOLUME_PNG));


        currentTimeLabel.setTextFill(Paint.valueOf("white"));
        currentTimeLabel.setTranslateX(-200);


        hitArea.setTranslateX(-50);

        volumeSlider.setTranslateX(-60);
        volumeSlider.setMaxWidth(130);
        volumeSlider.setVisible(false);

        slider.setMax(100);
        slider.setTooltip(tooltip);

        WatchTogetherApplication.getStage().getScene().setOnKeyPressed(videoKeyHandler::handleKeyPressed);


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setSceneWidth(double sceneWidth) {
        this.sceneWidth = sceneWidth;
    }

    public void setSceneHeight(double sceneHeight) {
        this.sceneHeight = sceneHeight;
    }

    public MediaView getMediaView() {
        return mediaView;
    }


    public Label getCurrentTimeLabel() {
        return currentTimeLabel;
    }

    public Slider getSlider() {
        return slider;
    }


    public VBox getVideoControlHBox() {
        return videoControlHBox;
    }


    public ImageView getChatImageView() {
        return chatImageView;
    }


    public ImageView getFullScreenView() {
        return fullScreenView;
    }


    public ImageView getPlayImageView() {
        return playImageView;
    }


    public VBox getParticipantsVBox() {
        return participantsVBox;
    }


    public TextField getChatInputField() {
        return chatInputField;
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }


    public Timeline getAnimation() {
        return animation;
    }


    public double getSceneWidth() {
        return sceneWidth;
    }


    public double getScale() {
        return scale;
    }

    public ComboBox<String> getParticipantsComboBox() {
        return participantsComboBox;
    }

    public VideoPlayerController getVideoPlayerController() {
        return videoPlayerController;
    }

    public VideoKeyHandler getVideoKeyHandler() {
        return videoKeyHandler;
    }

    public TextFlow getChatTextFlow() {
        return chatTextFlow;
    }

    public ScrollPane getChatScrollPane() {
        return chatScrollPane;
    }

    public ImageView getExitView() {
        return exitView;
    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    public Rectangle getHitArea() {
        return hitArea;
    }

    public Label getVolumeLabel() {
        return volumeLabel;
    }

    public BorderPane getBorderMediaPane() {
        return borderMediaPane;
    }

    public VideoVolumeController getVideoVolumeController() {
        return videoVolumeController;
    }

    public Label getVideoTitleLabel() {
        return videoTitleLabel;
    }

    public ImageView getShareImageView() {
        return shareImageView;
    }
}