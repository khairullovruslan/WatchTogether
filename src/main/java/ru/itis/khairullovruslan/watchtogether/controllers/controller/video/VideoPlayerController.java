package ru.itis.khairullovruslan.watchtogether.controllers.controller.video;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import ru.itis.khairullovruslan.watchtogether.WatchTogetherApplication;
import ru.itis.khairullovruslan.watchtogether.client.MessageReceiverController;
import ru.itis.khairullovruslan.watchtogether.client.WatchTogetherClient;
import ru.itis.khairullovruslan.watchtogether.constants.FxmlConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.view.VideoPlayerView;
import ru.itis.khairullovruslan.watchtogether.model.net.Message;
import ru.itis.khairullovruslan.watchtogether.util.ScreenVisualizer;


public class VideoPlayerController implements MessageReceiverController {
    private PauseTransition vBoxPause;
    private static final double ANIMATION_DURATION_FULLSCREEN = 200;
    private final ChatController chatController;
    private final VideoController videoController;
    private final VideoPlayerView videoPlayerView;

    public VideoPlayerController(VideoPlayerView videoPlayerView) {
        this.chatController = new ChatController(videoPlayerView);
        this.videoController = new VideoController(this);
        WatchTogetherApplication.getClient().setVideoController(this);
        this.videoPlayerView = videoPlayerView;
    }

    public void setupVBoxVisibility() {
        vBoxPause = new PauseTransition(Duration.seconds(10));
        vBoxPause.setOnFinished(event -> videoPlayerView.getVideoControlHBox().setVisible(false));

        videoPlayerView.getMediaView().setOnMouseEntered(event -> {
            videoPlayerView.getVideoControlHBox().setVisible(true);
            vBoxPause.playFromStart();
        });


        videoPlayerView.getMediaView().setOnMouseExited(event -> vBoxPause.play());

        videoPlayerView.getMediaView().setVisible(true);
        vBoxPause.play();
    }

    public void addListenersToUserAction() {
        videoController.addListenersToUserAction();

        videoPlayerView.getExitView().setOnMouseClicked(mouseEvent -> close());

        videoPlayerView.getBorderMediaPane().setOnMouseClicked(event -> {
            videoPlayerView.getBorderMediaPane().requestFocus();
        });

    }


    public void addUser(String name) {
        Platform.runLater(() -> videoPlayerView.getParticipantsComboBox().getItems().add(name));

    }

    @Override
    public void receiveMessage(Message message) {
        switch (message.getType()) {
            case "CLIENT_LIST" -> updateParticipantsList(message);
            case "JOINED_ROOM" -> addUser(message.getData());
            case "PAUSE" -> videoController.handlePause(message);
            case "PLAY" -> videoController.handlePlay(message);
            case "GET_HOST_MEDIA_TIME" -> sendMediaTime();
            case "SET_HOST_MEDIA_TIME" -> setHostMediaTime(message);
            case "CHAT_MESSAGE" -> chatController.getChatMessageFromServer(message);
            case "REMOVE_USER" -> chatController.removeUser(message);
            case "KICK_USER" -> kicked();
            case "REMOVE_HOST_USER" -> hostLeaved();
            case "CHANGE_TITLE" -> changeTitle(message);
            case "ROOM_INFO" -> {
                chatController.getChatMessageFromServer(message);
                videoController.handleShare(message);
            }
            case "CHAT_ERROR" -> chatController.giveServiceMessage(chatController.errorMessage(message));
        }
    }

    private void changeTitle(Message message) {
        Platform.runLater(() -> videoPlayerView.getVideoTitleLabel().setText(message.getData()));
    }

    private void sendMediaTime() {
        WatchTogetherClient client = WatchTogetherApplication.getClient();
        client.sendMessage("HOST_MEDIA_TIME:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(),
                videoPlayerView.getSlider().getValue() / 100.0));

    }

    private void setHostMediaTime(Message message) {
        double seekValue = videoPlayerView.getSlider().getValue();
        double hostSeek = Double.parseDouble(message.getData());
        if (Math.abs(hostSeek - seekValue) > 0.6) {
            videoPlayerView.getSlider().setValue(hostSeek * 100);
            videoPlayerView.getMediaPlayer().seek(videoPlayerView.getMediaPlayer().getTotalDuration().multiply(hostSeek));

        }
    }

    private void updateParticipantsList(Message message) {
        Platform.runLater(() -> {
            videoPlayerView.getParticipantsComboBox().getItems().clear();
            videoPlayerView.getParticipantsComboBox().getItems().addAll(message.getData().split(";"));
        });
    }


    public void animateMediaViewScale(double scale) {
        KeyValue mediaScaleX = new KeyValue(videoPlayerView.getMediaView().scaleXProperty(), scale);
        KeyValue mediaScaleY = new KeyValue(videoPlayerView.getMediaView().scaleYProperty(), scale);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(ANIMATION_DURATION_FULLSCREEN), mediaScaleX, mediaScaleY);
        videoPlayerView.getAnimation().getKeyFrames().add(keyFrame);
        videoPlayerView.getAnimation().play();
    }


    public void animateMediaView(double scale, double translateX, int durationMillis) {
        KeyValue mediaScaleX = new KeyValue(videoPlayerView.getMediaView().scaleXProperty(), scale);
        KeyValue mediaScaleY = new KeyValue(videoPlayerView.getMediaView().scaleYProperty(), scale);
        KeyValue mediaKeyValue = new KeyValue(videoPlayerView.getMediaView().translateXProperty(), translateX);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(durationMillis), mediaScaleX, mediaScaleY, mediaKeyValue);

        videoPlayerView.getAnimation().getKeyFrames().add(keyFrame);
        videoPlayerView.getAnimation().play();
    }


    public void kicked() {
        videoPlayerView.getMediaPlayer().dispose();
        ScreenVisualizer.show(FxmlConstants.PRE_WATCH_SETUP_SCREEN);
        WatchTogetherClient client = WatchTogetherApplication.getClient();
        client.close();
    }

    public void close() {
        videoPlayerView.getMediaPlayer().dispose();
        ScreenVisualizer.show(FxmlConstants.PRE_WATCH_SETUP_SCREEN);
        WatchTogetherClient client = WatchTogetherApplication.getClient();
        client.sendMessage("CLOSE:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(), "nothing"));
        client.close();
    }

    public void hostLeaved() {
        videoPlayerView.getMediaPlayer().dispose();
        WatchTogetherClient client = WatchTogetherApplication.getClient();
        client.close();
        ScreenVisualizer.show(FxmlConstants.PRE_WATCH_SETUP_SCREEN);
    }

    public ChatController getChatController() {
        return chatController;
    }

    public PauseTransition getVBoxPause() {
        return vBoxPause;
    }

    public VideoPlayerView getVideoPlayerView() {
        return videoPlayerView;
    }

    public VideoController getVideoController() {
        return videoController;
    }
}