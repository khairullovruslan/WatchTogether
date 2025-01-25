package ru.itis.khairullovruslan.watchtogether.controllers.controller;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import ru.itis.khairullovruslan.watchtogether.WatchTogetherApplication;
import ru.itis.khairullovruslan.watchtogether.client.MessageReceiverController;
import ru.itis.khairullovruslan.watchtogether.client.WatchTogetherClient;
import ru.itis.khairullovruslan.watchtogether.constants.BundleConstants;
import ru.itis.khairullovruslan.watchtogether.constants.NetworkConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.view.JoinRoomView;
import ru.itis.khairullovruslan.watchtogether.model.net.Message;
import ru.itis.khairullovruslan.watchtogether.util.ScreenVisualizer;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.util.UUID;

public class JoinRoomController implements MessageReceiverController {


    private final JoinRoomView joinRoomView;

    public JoinRoomController(JoinRoomView joinRoomView) {
        this.joinRoomView = joinRoomView;
    }


    public void setMouseClickedEvents() {
        joinRoomView.getJoinRoomLabel().setOnMouseClicked(event -> handleJoinRoom());
        joinRoomView.getInsertView().setOnMouseClicked(mouseEvent -> insertData());

    }

    //share:code-password
    private void insertData() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            String[] data = clipboard.getString().split(":");
            if (data.length == 2) {
                String[] roomInfo = data[1].split("-");
                if (roomInfo.length == 2) {
                    joinRoomView.getCodeField().setText(roomInfo[0]);
                    joinRoomView.getPasswordField().setText(roomInfo[1]);
                    return;
                }
            }

        }
        joinRoomView.getErrorLabel().setVisible(true);
        joinRoomView.getErrorLabel().setText(LocalizationUtil.getBundle().getString(BundleConstants.FAILED_TO_INSERT_PWD_AND_CODE));
    }


    @Override
    public void receiveMessage(Message message) {
        System.out.println(message);
        if ("VIDEO_URL".equals(message.getType())) {
            ScreenVisualizer.showVideo(message.getData());
        } else if ("ROOM_CODE_AND_PASSWORD".equals(message.getType())) {
            WatchTogetherApplication.getClient().setRoomCode(message.getData().split(";")[0]);
        } else if ("JOIN_ERROR".equals(message.getType())) {
            Platform.runLater(() -> {
                Label errorLabel = joinRoomView.getErrorLabel();
                errorLabel.setVisible(true);
                errorLabel.setText(message.getData());

            });
        }
    }

    private void handleJoinRoom() {

        try {
            connectToRoom();
        } catch (NumberFormatException e) {
            System.err.println("Неверный номер порта.");
        } catch (Exception e) {
            System.err.println("Ошибка при подключении к серверу: " + e.getMessage());
        }
    }


    private void connectToRoom() {
        WatchTogetherClient client = new WatchTogetherClient(NetworkConstants.SERVER_ADDRESS, NetworkConstants.PORT, UUID.randomUUID());
        WatchTogetherApplication.setClient(client);
        client.setController(this);
        client.connect();
        client.listenForResponses();

        sendJoinRoomMessage(client);
    }


    private void sendJoinRoomMessage(WatchTogetherClient client) {
        //type:clientId:roomId:password;name
        String name = joinRoomView.getNameField().getText();
        String password = joinRoomView.getPasswordField().getText();
        String code = joinRoomView.getCodeField().getText();
        client.sendMessage("JOIN_ROOM:%s:%s:%s;%s".formatted(client.getClientId(), code, password, name));
    }


}


