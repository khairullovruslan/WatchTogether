package ru.itis.khairullovruslan.watchtogether.controllers.controller.video;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.itis.khairullovruslan.watchtogether.WatchTogetherApplication;
import ru.itis.khairullovruslan.watchtogether.client.WatchTogetherClient;
import ru.itis.khairullovruslan.watchtogether.constants.BundleConstants;
import ru.itis.khairullovruslan.watchtogether.constants.StyleConstants;
import ru.itis.khairullovruslan.watchtogether.model.net.Message;
import ru.itis.khairullovruslan.watchtogether.util.UIHelper;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

public class VideoController {
    private boolean isDarkTheme = false;
    private boolean sliderBeingDragged = false;
    private boolean chatVisible = false;
    private boolean isFullScreen = false;
    private boolean isPlay = false;
    private final VideoPlayerController videoPlayerController;


    public VideoController(VideoPlayerController videoPlayerController) {
        this.videoPlayerController = videoPlayerController;
    }

    public void addListenersToUserAction() {
        videoPlayerController.getVideoPlayerView().getSlider().setOnMousePressed(e -> sliderBeingDragged = true);
        videoPlayerController.getVideoPlayerView().getSlider().setOnMouseReleased(e -> {
            if (videoPlayerController.getVideoPlayerView().getMediaPlayer() != null && videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration() != null) {
                double seekValue = videoPlayerController.getVideoPlayerView().getSlider().getValue() / 100.0;
                videoPlayerController.getVideoPlayerView().getMediaPlayer().seek(videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration().multiply(seekValue));
                WatchTogetherClient client = WatchTogetherApplication.getClient();
                client.sendMessage("MEDIA_TIME:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(),
                        videoPlayerController.getVideoPlayerView().getSlider().getValue() / 100.0));
            }
            sliderBeingDragged = false;
        });

        videoPlayerController.getVideoPlayerView().getSlider().setOnMouseMoved(this::handleSliderMouseMoved);
        videoPlayerController.getVideoPlayerView().getSlider().setOnMouseExited(e -> videoPlayerController.getVideoPlayerView().getTooltip().hide());

        videoPlayerController.getVideoPlayerView().getMediaPlayer().currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!sliderBeingDragged) {
                updateSliderAndTimeLabels(newTime);
            }
        });

        videoPlayerController.getVideoPlayerView().getChatInputField().setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("TAB")) {
                event.consume();
                videoPlayerController.getVideoPlayerView().getChatInputField().requestFocus();
                videoPlayerController.getVideoPlayerView().getVideoKeyHandler().handleTabPressed();
                videoPlayerController.getVideoPlayerView().getChatInputField().positionCaret(videoPlayerController.getVideoPlayerView().getChatInputField().getText().length());
            }
        });
        videoPlayerController.getVideoPlayerView().getPlayImageView().setOnMouseClicked(mouseEvent -> handlePlay());
        videoPlayerController.getVideoPlayerView().getChatImageView().setOnMouseClicked(mouseEvent -> handleShowChat());
        videoPlayerController.getVideoPlayerView().getFullScreenView().setOnMouseClicked(mouseEvent -> handleFullScreen());
        videoPlayerController.getVideoPlayerView().getShareImageView().setOnMouseClicked(mouseEvent -> handleShare());
    }

    private void handleShare() {
        WatchTogetherClient client = WatchTogetherApplication.getClient();
        client.sendMessage("GET_ROOM_INFO:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(), "nothing"));

    }
    public void handleShare(Message message) {
        Platform.runLater(() -> {

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString("share:%s".formatted(message.getData()));
            clipboard.setContent(content);
            showShareAnimation();
        });


    }

    private void updateSliderAndTimeLabels(javafx.util.Duration newTime) {
        double currentTimePercentage = (newTime.toMillis() / videoPlayerController.getVideoPlayerView()
                .getMediaPlayer().getTotalDuration().toMillis()) * 100;
        videoPlayerController.getVideoPlayerView().getSlider().setValue(currentTimePercentage);
        videoPlayerController.getVideoPlayerView().getCurrentTimeLabel().setText(String.format("%02d:%02d / %02d:%02d",
                (int) newTime.toMinutes(),
                (int) newTime.toSeconds() % 60,
                (int) videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration().toMinutes(),
                (int) videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration().toSeconds() % 60));

    }

    public void handlePlay(Message message) {
        if (message.getData() != null) {
            double pauseTime = Double.parseDouble(message.getData());
            videoPlayerController.getVideoPlayerView().getSlider().setValue(pauseTime * 100);
            videoPlayerController.getVideoPlayerView().getMediaPlayer().seek(
                    videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration().multiply(pauseTime));
            videoPlayerController.getVideoPlayerView().getMediaPlayer().play();
            videoPlayerController.getVideoPlayerView().getPlayImageView().setImage(UIHelper.getImage(StyleConstants.PAUSE_PNG));

        }
    }

    public void handlePause(Message message) {
        if (message.getData() != null) {
            double pauseTime = Double.parseDouble(message.getData());
            videoPlayerController.getVideoPlayerView().getMediaPlayer().pause();
            videoPlayerController.getVideoPlayerView().getPlayImageView().setImage(UIHelper.getImage(StyleConstants.PLAY_PNG));
            videoPlayerController.getVideoPlayerView().getSlider().setValue(pauseTime * 100);
            videoPlayerController.getVideoPlayerView().getMediaPlayer()
                    .seek(videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration().multiply(pauseTime));
        }
    }

    private void handleSliderMouseMoved(MouseEvent event) {
        if (videoPlayerController.getVideoPlayerView().getMediaPlayer() == null ||
                videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration() == null)
            return;
        Duration duration = videoPlayerController.getVideoPlayerView().getMediaPlayer()
                .getTotalDuration().multiply(videoPlayerController.getVideoPlayerView().getSlider().getValue() / 100.0);


        videoPlayerController.getVideoPlayerView().getTooltip().setText(
                String.format("%02d:%02d", (int) duration.toMinutes(), (int) duration.toSeconds() % 60));
        double x = event.getScreenX();
        double y = event.getScreenY() + 20;
        videoPlayerController.getVideoPlayerView().getTooltip().show(videoPlayerController.getVideoPlayerView().getSlider(), x, y);
    }

    public void handlePlay() {
        WatchTogetherClient client = WatchTogetherApplication.getClient();
        double seekValue = videoPlayerController.getVideoPlayerView().getSlider().getValue() / 100.0;
        if (isPlay) {
            client.sendMessage("PAUSE:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(), seekValue));
            videoPlayerController.getVideoPlayerView().getPlayImageView().setImage(UIHelper.getImage(StyleConstants.PLAY_PNG));
            videoPlayerController.getVideoPlayerView().getMediaPlayer().pause();
        } else {
            client.sendMessage("PLAY:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(), seekValue));
            videoPlayerController.getVideoPlayerView().getPlayImageView().setImage(UIHelper.getImage(StyleConstants.PAUSE_PNG));
            videoPlayerController.getVideoPlayerView().getMediaPlayer().play();
        }
        isPlay = !isPlay;

    }

    private void handleFullScreen() {
        if (chatVisible) return;
        videoPlayerController.getVideoPlayerView().getAnimation().stop();
        videoPlayerController.getVideoPlayerView().getAnimation().getKeyFrames().clear();
        double mediaViewWidth = videoPlayerController.getVideoPlayerView().getMediaView().getBoundsInLocal().getWidth();
        double scale = isFullScreen ? videoPlayerController.getVideoPlayerView().getScale() : videoPlayerController.getVideoPlayerView().getSceneWidth() / mediaViewWidth;
        videoPlayerController.getVideoPlayerView().getChatImageView().setVisible(isFullScreen);
        videoPlayerController.animateMediaViewScale(scale);

        videoPlayerController.getVideoPlayerView().getExitView().setVisible(isFullScreen);
        videoPlayerController.getVideoPlayerView().getVideoTitleLabel().setVisible(isFullScreen);

        isFullScreen = !isFullScreen;
        handleThemeSwitch();


    }

    public void handleThemeSwitch() {
        Stage stage = WatchTogetherApplication.getStage();
        if (stage == null || stage.getScene() == null) return;

        isDarkTheme = !isDarkTheme;
        String backgroundColor = isDarkTheme ? "#000000FF" : "#201f23";
        stage.getScene().getRoot().setStyle("-fx-background-color: %s;".formatted(backgroundColor));

    }

    private void handleShowChat() {
        videoPlayerController.getVideoPlayerView().getAnimation().stop();
        videoPlayerController.getVideoPlayerView().getAnimation().getKeyFrames().clear();
        if (!chatVisible) {
            videoPlayerController.animateMediaView(videoPlayerController.getVideoPlayerView().getScale() * 0.77, -170, 200);
            videoPlayerController.getVideoPlayerView().getParticipantsVBox().setVisible(true);
            chatVisible = true;
        } else {
            videoPlayerController.animateMediaView(videoPlayerController.getVideoPlayerView().getScale(), 0, 170);
            videoPlayerController.getVideoPlayerView().getParticipantsVBox().setVisible(false);
            chatVisible = false;
        }
    }

    public void skipForward() {
        if (videoPlayerController.getVideoPlayerView().getMediaPlayer() != null) {
            double currentTimeInSeconds = videoPlayerController.getVideoPlayerView().getMediaPlayer().getCurrentTime().toSeconds();
            double newTimeInSeconds = Math.min(currentTimeInSeconds + 10,
                    videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration().toSeconds());
            videoPlayerController.getVideoPlayerView().getMediaPlayer().seek(Duration.seconds(newTimeInSeconds));
            videoPlayerController.getVideoPlayerView().getSlider().setValue(newTimeInSeconds /
                    videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration().toSeconds() * 100);
            WatchTogetherClient client = WatchTogetherApplication.getClient();
            client.sendMessage("MEDIA_TIME:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(),
                    videoPlayerController.getVideoPlayerView().getSlider().getValue() / 100.0));
        }
    }

    public void skipBackward() {
        if (videoPlayerController.getVideoPlayerView().getMediaPlayer() != null) {
            double currentTimeInSeconds = videoPlayerController.getVideoPlayerView().getMediaPlayer().getCurrentTime().toSeconds();
            double newTimeInSeconds = Math.max(currentTimeInSeconds - 10, 0);
            videoPlayerController.getVideoPlayerView().getMediaPlayer().seek(Duration.seconds(newTimeInSeconds));
            videoPlayerController.getVideoPlayerView().getSlider().setValue(newTimeInSeconds /
                    videoPlayerController.getVideoPlayerView().getMediaPlayer().getTotalDuration().toSeconds() * 100);
            WatchTogetherClient client = WatchTogetherApplication.getClient();
            client.sendMessage("MEDIA_TIME:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(),
                    videoPlayerController.getVideoPlayerView().getSlider().getValue() / 100.0));
        }
    }

    private void showShareAnimation() {
        videoPlayerController.getVideoPlayerView()
                .getVolumeLabel().setText(LocalizationUtil.getBundle().getString(BundleConstants.DATA_SUCCESSFULLY_SAVED_TO_BUFFER));
        videoPlayerController.getVideoPlayerView().getVolumeLabel().setOpacity(1);

        FadeTransition fadeTransition = new FadeTransition(
                Duration.seconds(1), videoPlayerController.getVideoPlayerView().getVolumeLabel());
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }



}
