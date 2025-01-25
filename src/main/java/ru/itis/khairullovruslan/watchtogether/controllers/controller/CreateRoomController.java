package ru.itis.khairullovruslan.watchtogether.controllers.controller;

import ru.itis.khairullovruslan.watchtogether.WatchTogetherApplication;
import ru.itis.khairullovruslan.watchtogether.client.MessageReceiverController;
import ru.itis.khairullovruslan.watchtogether.client.WatchTogetherClient;
import ru.itis.khairullovruslan.watchtogether.constants.NetworkConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.view.CreateRoomView;
import ru.itis.khairullovruslan.watchtogether.model.net.Message;
import ru.itis.khairullovruslan.watchtogether.util.ScreenVisualizer;

import java.net.URI;
import java.util.UUID;


public class CreateRoomController implements MessageReceiverController {


    private final CreateRoomView createRoomView;


    public CreateRoomController(CreateRoomView createRoomView) {
        this.createRoomView = createRoomView;
    }

    public void setMouseClickedEvents() {
        createRoomView.getCreateRoomLabel().setOnMouseClicked(mouseEvent -> handleRoomCreation());

    }

    @Override
    public void receiveMessage(Message message) {
        if ("VIDEO_URL".equals(message.getType())) {
            ScreenVisualizer.showVideo(message.getData());
        } else if ("ROOM_CODE_AND_PASSWORD".equals(message.getType())) {
            System.out.println(message);
            WatchTogetherApplication.getClient().setRoomCode(message.getData().split(";")[0]);
        } else {
            System.out.println(message);
        }
    }


    private void handleRoomCreation() {
//        WatchTogetherServerProvider.getUnavailablePorts();
        String videoUrl = getVideoUrl();
        if (isValidUrl(videoUrl)) {
            createAndStartServer(videoUrl);
        } else {
            System.err.println("Incorrect port or URL");
        }
    }

    private String getVideoUrl() {
        String url = createRoomView.getVideoLinkField().getText();
        return url.isEmpty() ?
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" :
                url;
    }

    private void connectToRoom(String videoUrl) {
        WatchTogetherClient client = new WatchTogetherClient(NetworkConstants.SERVER_ADDRESS, NetworkConstants.PORT, UUID.randomUUID());
        WatchTogetherApplication.setClient(client);
        client.setController(this);
        client.connect();
        client.listenForResponses();

        sendCreateRoomMessage(client, videoUrl);
    }

    private void sendCreateRoomMessage(WatchTogetherClient client, String videoUrl) {
        client.sendMessage("CREATE_ROOM:%s:%s:%s".formatted(client.getClientId(), UUID.randomUUID(), videoUrl));
    }

    private void createAndStartServer(String videoUrl) {
        connectToRoom(videoUrl);
    }

    private boolean isValidUrl(String url) {
        try {
            URI.create(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
