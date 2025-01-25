package ru.itis.khairullovruslan.watchtogether.controllers.controller.video;

import javafx.scene.input.KeyEvent;
import ru.itis.khairullovruslan.watchtogether.constants.ChatCommand;
import ru.itis.khairullovruslan.watchtogether.controllers.view.VideoPlayerView;

import java.util.List;
import java.util.stream.Collectors;

public class VideoKeyHandler {
    private final VideoPlayerView videoPlayerView;

    public VideoKeyHandler(VideoPlayerView videoPlayerView) {
        this.videoPlayerView = videoPlayerView;
    }

    public void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER -> videoPlayerView.getVideoPlayerController().getChatController().handleSendChat();
            case SPACE -> videoPlayerView.getVideoPlayerController().getVideoController().handlePlay();
            case UP -> videoPlayerView.getVideoVolumeController().addVolume();
            case DOWN -> videoPlayerView.getVideoVolumeController().turnDownTheVolume();
            case RIGHT -> videoPlayerView.getVideoPlayerController().getVideoController().skipForward();
            case LEFT -> videoPlayerView.getVideoPlayerController().getVideoController().skipBackward();
            default -> System.out.println(event.getCode());
        }
    }

    public void handleTabPressed() {
        videoPlayerView.getChatInputField().setText(videoPlayerView.getChatInputField().getText()
                + findSecondWordPart(getLastWord(videoPlayerView.getChatInputField().getText())));

    }

    public static String getLastWord(String str) {
        int lastIndex = str.lastIndexOf(' ');
        if (lastIndex == -1) {
            return str;
        }
        return str.substring(lastIndex + 1);
    }

    public String findSecondWordPart(String message) {
        List<String> words = videoPlayerView.getParticipantsComboBox()
                .getItems().stream().filter(item -> item.startsWith(message)).collect(Collectors.toList());
        if (words.isEmpty()) {
            for (ChatCommand command : ChatCommand.values()) {
                if (command.getCommand().startsWith(message)) {
                    words.add(command.getCommand());
                }
            }
            if (words.isEmpty()) return "";
        }
        videoPlayerView.getVideoPlayerController().getChatController().giveServiceMessage(
                videoPlayerView.getVideoPlayerController().getChatController().possibleCommandsMessage(words)
        );
        return words.get(0).substring(message.length());

    }

}