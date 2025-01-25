package ru.itis.khairullovruslan.watchtogether.controllers.controller.video;

import javafx.application.Platform;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ru.itis.khairullovruslan.watchtogether.WatchTogetherApplication;
import ru.itis.khairullovruslan.watchtogether.client.WatchTogetherClient;
import ru.itis.khairullovruslan.watchtogether.constants.BundleConstants;
import ru.itis.khairullovruslan.watchtogether.constants.ChatCommand;
import ru.itis.khairullovruslan.watchtogether.controllers.view.VideoPlayerView;
import ru.itis.khairullovruslan.watchtogether.model.net.Message;
import ru.itis.khairullovruslan.watchtogether.util.UIHelper;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChatController {
    private final VideoPlayerView videoPlayerView;

    public ChatController(VideoPlayerView videoPlayerView) {
        this.videoPlayerView = videoPlayerView;
    }

    public void handleSendChat() {
        String message = videoPlayerView.getChatInputField().getText();
        if (message.startsWith("/")) {
            handleCommandMessage(message);
            return;

        }
        if (!message.trim().isEmpty()) {

            WatchTogetherClient client = WatchTogetherApplication.getClient();
            client.sendMessage("CHAT_MESSAGE:%s:%s:%s;%s".formatted(client.getClientId(), client.getRoomCode(),
                    WatchTogetherApplication.getClient().getMessageColor(), message));

            videoPlayerView.getChatInputField().clear();
        }
    }

    private void handleCommandMessage(String message) {
        String[] words = message.split(" ");
        ChatCommand command = ChatCommand.fromString(words[0]);
        System.out.println(command);
        switch (Objects.requireNonNull(command)) {
            case HELP:
                giveServiceMessage(generateHelpText());
                break;
            case INFO:
                generateInfoText();
                break;
            case TITLE:
                generateTitleText(words);
                break;
            case KICK:
                generateKickText(words);
                break;
            default:
                System.out.println("неизвестная команда");
        }

    }

    private void generateKickText(String[] words) {
        if (words.length == 2) {
            WatchTogetherClient client = WatchTogetherApplication.getClient();
            client.sendMessage("KICK:%s:%s:%s".formatted(client.getClientId(),
                    client.getRoomCode(), words[1]));

        }
    }

    private void generateInfoText() {
        WatchTogetherClient client = WatchTogetherApplication.getClient();
        client.sendMessage("GET_ROOM_INFO:%s:%s:%s".formatted(client.getClientId(), client.getRoomCode(), "nothing"));

    }

    private void generateTitleText(String[] message) {

        if (message.length > 1) {
            WatchTogetherClient client = WatchTogetherApplication.getClient();
            client.sendMessage("SET_ROOM_TITLE:%s:%s:%s".formatted(client.getClientId(),
                    client.getRoomCode(),
                    String.join(" ", Arrays.copyOfRange(message, 1, message.length))));

        }

    }

    private List<Text> generateHelpText() {
        List<Text> textList = new ArrayList<>();
        textList.add(getServerText());
        for (ChatCommand command : ChatCommand.values()) {
            Text commandText = new Text("%s - ".formatted(command.getCommand()));
            commandText.setFont(Font.font("Inter", FontWeight.BOLD, 16));
            commandText.setFill(Paint.valueOf("yellow"));

            Text descText = new Text("%s\n".formatted(command.getDescription()));
            descText.setFont(Font.font("Inter", FontPosture.ITALIC, 15));
            descText.setFill(Paint.valueOf("white"));
            textList.add(commandText);
            textList.add(descText);
        }
        return textList;

    }

    private Text getServerText() {
        Text server = new Text("SERVER:\n");
        server.setFont(UIHelper.getFont(16));
        server.setFill(Paint.valueOf("red"));
        return server;
    }

    public void giveServiceMessage(List<Text> message) {

        Platform.runLater(() -> {
            videoPlayerView.getChatTextFlow().getChildren().addAll(message);
            videoPlayerView.getChatScrollPane().setVvalue(1.0d);
        });
    }

    public void getChatMessageFromServer(Message message) {
        switch (message.getType()) {
            case "CHAT_MESSAGE":
                handleChatMessage(message);
                break;

            case "ROOM_INFO":
                handleRoomInfo(message);
                break;

            default:
                break;
        }
    }

    private void handleChatMessage(Message message) {
        String[] data = message.getData().split(":", 3);

        if (data.length < 3) {
            System.err.println("Неверные данные в сообщении чата.");
            return;
        }

        Text nickname = createText(data[0] + ": ", UIHelper.getFont(16), Paint.valueOf(data[1]));
        Text mess = createText(data[2] + "\n", Font.font("Inter", 16), Paint.valueOf("white"));

        Platform.runLater(() -> {
            videoPlayerView.getChatTextFlow().getChildren().addAll(nickname, mess);
            videoPlayerView.getChatScrollPane().setVvalue(1.0d);
        });
    }

    private void handleRoomInfo(Message message) {
        String[] data = message.getData().split("-");

        if (data.length < 2) {
            System.err.println("Неверные данные в сообщении о комнате.");
            return;
        }

        List<Text> textList = new ArrayList<>();
        textList.add(getServerText());

        textList.add(createText("%s - ".formatted(LocalizationUtil.getBundle().getString(BundleConstants.CODE)), Font.font("Inter", FontWeight.BOLD, 16), Paint.valueOf("yellow")));
        textList.add(createText(data[0] + "\n", Font.font("Inter", FontPosture.ITALIC, 15), Paint.valueOf("white")));
        textList.add(createText("%s - ".formatted(LocalizationUtil.getBundle().getString(BundleConstants.PASSWORD)), Font.font("Inter", FontWeight.BOLD, 16), Paint.valueOf("yellow")));
        textList.add(createText(data[1] + "\n", Font.font("Inter", FontPosture.ITALIC, 15), Paint.valueOf("white")));

        giveServiceMessage(textList);
    }

    private Text createText(String content, Font font, Paint color) {
        Text text = new Text(content);
        text.setFont(font);
        text.setFill(color);
        return text;
    }


    public void removeUser(Message message) {
        videoPlayerView.getParticipantsComboBox().getItems().removeIf(user -> user.equals(message.getData()));
    }

    public List<Text> errorMessage(Message message) {
        List<Text> textList = new ArrayList<>();
        textList.add(getServerText());
        textList.add(createText("%s\n".formatted(message.getData()), Font.font("Inter", FontWeight.BOLD, 16), Paint.valueOf("white")));

        return textList;

    }

    public List<Text> possibleCommandsMessage(List<String> words) {
        List<Text> textList = new ArrayList<>();
        textList.add(getServerText());
        textList.add(createText("%s\n".formatted(String.join(", ", words)), Font.font("Inter", 16), Paint.valueOf("white")));

        return textList;
    }
}
