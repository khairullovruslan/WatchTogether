package ru.itis.khairullovruslan.watchtogether.controllers.controller.video;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import ru.itis.khairullovruslan.watchtogether.constants.BundleConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.view.VideoPlayerView;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

public class VideoVolumeController {

    private PauseTransition pause;
    private boolean volumeSliderBeingDragged = false;
    private final VideoPlayerView videoPlayerView;

    public VideoVolumeController(VideoPlayerView videoPlayerView) {
        this.videoPlayerView = videoPlayerView;
    }

    public void addListenersToUserAction() {
        setHitAreaListeners();
        setVolumeSliderListeners();
        setVolumeSliderValueChangeListener();
    }

    private void setHitAreaListeners() {
        videoPlayerView.getHitArea().setOnMouseEntered(mouseEvent -> handleVolumeShow());
        videoPlayerView.getHitArea().setOnMouseExited(mouseEvent -> handleVolumeHide());

        pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> handleVolumeHideTransition());
    }

    private void handleVolumeHideTransition() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), videoPlayerView.getVolumeSlider());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            videoPlayerView.getCurrentTimeLabel().setTranslateX(-200);
            videoPlayerView.getVolumeSlider().setVisible(false);
        });
        fadeOut.play();
    }

    private void setVolumeSliderListeners() {
        videoPlayerView.getVolumeSlider().setOnMouseEntered(e -> {
            pause.playFromStart();
            videoPlayerView.getVideoPlayerController().getVBoxPause().playFromStart();
        });
        videoPlayerView.getVolumeSlider().setOnMouseMoved(e -> {
            pause.playFromStart();
            videoPlayerView.getVideoPlayerController().getVBoxPause().playFromStart();

        });

        videoPlayerView.getVolumeSlider().setOnMousePressed(e -> {
            volumeSliderBeingDragged = true;
            videoPlayerView.getVideoPlayerController().getVBoxPause().playFromStart();
            pause.playFromStart();
        });

        videoPlayerView.getVolumeSlider().setOnMouseReleased(e -> handleVolumeSliderRelease());

        videoPlayerView.getVolumeSlider().setOnMouseDragged(e -> {
            handleVolumeSliderDrag();
            videoPlayerView.getVideoPlayerController().getVBoxPause().playFromStart();
        });
    }

    private void handleVolumeSliderRelease() {
        if (videoPlayerView.getMediaPlayer() != null) {
            double volumeValue = videoPlayerView.getVolumeSlider().getValue() / 100.0;
            videoPlayerView.getMediaPlayer().setVolume(volumeValue);
        }
        volumeSliderBeingDragged = false;
    }

    private void handleVolumeSliderDrag() {
        if (videoPlayerView.getMediaPlayer() != null) {
            double volumeValue = videoPlayerView.getVolumeSlider().getValue() / 100.0;
            videoPlayerView.getMediaPlayer().setVolume(volumeValue);
        }
    }

    private void setVolumeSliderValueChangeListener() {
        videoPlayerView.getVolumeSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!volumeSliderBeingDragged) {
                double volumeValue = newVal.doubleValue() / 100.0;
                if (videoPlayerView.getMediaPlayer() != null) {
                    videoPlayerView.getMediaPlayer().setVolume(volumeValue);
                }
            }
            showVolumeAnimation(newVal.doubleValue());
        });
    }

    private void handleVolumeShow() {
        if (!videoPlayerView.getVolumeSlider().isVisible()) {
            videoPlayerView.getVolumeSlider().setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), videoPlayerView.getVolumeSlider());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            videoPlayerView.getCurrentTimeLabel().setTranslateX(-65);
        }
        pause.playFromStart();
    }

    private void handleVolumeHide() {
        pause.play();
    }

    public void addVolume() {
        adjustVolume(5);
    }

    public void turnDownTheVolume() {
        adjustVolume(-5);
    }

    private void adjustVolume(double delta) {
        double currentVolume = videoPlayerView.getVolumeSlider().getValue();
        double newVolume = Math.max(0, Math.min(currentVolume + delta, 100));
        videoPlayerView.getVolumeSlider().setValue(newVolume);
        if (videoPlayerView.getMediaPlayer() != null) {
            videoPlayerView.getMediaPlayer().setVolume(newVolume / 100.0);
        }
    }

    private void showVolumeAnimation(double volume) {
        videoPlayerView.getVolumeLabel()
                .setText("%s: %d".formatted(LocalizationUtil.getBundle().getString(BundleConstants.VOLUME), (int) volume));
        videoPlayerView.getVolumeLabel().setOpacity(1);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), videoPlayerView.getVolumeLabel());
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }
}
