package ru.itis.khairullovruslan.watchtogether.server.handler;

import ru.itis.khairullovruslan.watchtogether.constants.BundleConstants;
import ru.itis.khairullovruslan.watchtogether.model.net.Room;
import ru.itis.khairullovruslan.watchtogether.server.UserMessage;
import ru.itis.khairullovruslan.watchtogether.server.WatchTogetherServer;
import ru.itis.khairullovruslan.watchtogether.util.NickNameValidator;
import ru.itis.khairullovruslan.watchtogether.util.RandomCodeGenerator;
import ru.itis.khairullovruslan.watchtogether.util.setting.LocalizationUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ClientHandler extends Thread {


    private final Socket clientSocket;
    private final WatchTogetherServer server;
    private UUID clientId;
    private String clientName;
    private PrintWriter out;


    private BufferedReader in;

    public ClientHandler(Socket clientSocket, WatchTogetherServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }


    @Override
    public void run() {
        try {
            this.clientId = UUID.randomUUID();

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Сообщение от клиента " + request);
                processRequest(request);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            stopClient();
        }
    }

    private void processRequest(String request) {

        UserMessage message = getMessage(request);
        switch (message.type()) {
            case "GET_VIDEO_URL":
                sendPrivateMessage(message.clientId(), "VIDEO_URL:%s".formatted(server.getRooms().get(message.roomId()).getVideoUrl()));
                break;
            case "CREATE_ROOM":
                server.removeClient(clientId);
                clientId = message.clientId();
                server.addClient(clientId, this);

                String code = RandomCodeGenerator.generateRandomCode();
                String password = RandomCodeGenerator.generateRandomCode();
                Room room = new Room(code, password, message.clientId(), message.message());

                clientName = "host";
                server.addRoom(room);
                server.roomAddClient(code, this);
                sendPrivateMessage(message.clientId(), "ROOM_CODE_AND_PASSWORD:%s;%s".formatted(code, password));
                sendPrivateMessage(message.clientId(), "VIDEO_URL:%s".formatted(room.getVideoUrl()));
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        sendPrivateMessage(clientId, "GET_HOST_MEDIA_TIME:nothing");
                    }
                }, 0, 10, TimeUnit.SECONDS);
                break;

            case "PAUSE":

                if (message.clientId().equals(server.getRooms().get(message.roomId()).getHostId())) {
                    broadcastMessage("PAUSE:%s".formatted(message.message()), message.roomId(), message.clientId());
                }
                break;
            case "GET_CLIENTS":
                sendPrivateMessage(message.clientId(), "CLIENT_LIST:%s".formatted(String.join(";",
                        server.getRooms().get(message.roomId()).getClients().stream().map(ClientHandler::getClientName).filter(Objects::nonNull).toList())));
                break;

            case "HOST_MEDIA_TIME":
                broadcastMessage("SET_HOST_MEDIA_TIME:%s".formatted(message.message()), message.roomId(), message.clientId());
                break;

            case "MEDIA_TIME":
                if (message.message().length() < 40 && message.clientId().equals(server.getRooms().get(message.roomId()).getHostId())){
                    broadcastMessage("SET_HOST_MEDIA_TIME:%s".formatted(message.message()), message.roomId(), message.clientId());
                }
                break;
            case "JOIN_ROOM":
                server.removeClient(clientId);
                clientId = message.clientId();
                server.addClient(clientId, this);

                String[] data = message.message().split(";", 2);
                if (!server.getRooms().containsKey(message.roomId())) {
                    sendPrivateMessage(message.clientId(), "JOIN_ERROR:%s"
                            .formatted(LocalizationUtil.getBundle().getString(BundleConstants.ROOM_NOT_FOUND)));
                    return;
                }
                Room room1 = server.getRooms().get(message.roomId());
                if (!room1.getPassword().equals(data[0])) {
                    sendPrivateMessage(message.clientId(), "JOIN_ERROR:%s"
                            .formatted(LocalizationUtil.getBundle().getString(BundleConstants.PASSWORD_INCORRECT)));

                    return;
                }
                if (room1.getClients().stream().map(ClientHandler::getClientName).collect(Collectors.toSet()).contains(data[1])) {
                    sendPrivateMessage(message.clientId(), "JOIN_ERROR:%s"
                            .formatted(LocalizationUtil.getBundle().getString(BundleConstants.NICKNAME_IS_TAKEN)));
                    return;
                }
                if (data[1].length() > 15) {
                    sendPrivateMessage(message.clientId(), "JOIN_ERROR:%s"
                            .formatted(LocalizationUtil.getBundle().getString(BundleConstants.THE_LENGTH_OF_THE_NICKNAME_MUST_BE_LESS)));
                    return;
                }
                if (!NickNameValidator.isValidate(data[1])) {
                    sendPrivateMessage(message.clientId(), "JOIN_ERROR:%s"
                            .formatted(LocalizationUtil.getBundle().getString(BundleConstants.NICKNAME_CONTAINS_INVALID_CHARS)));
                    return;
                }
                clientName = data[1];
                server.roomAddClient(message.roomId(), this);
                sendPrivateMessage(message.clientId(), "ROOM_CODE_AND_PASSWORD:%s;%s".formatted(room1.getCode(), room1.getPassword()));
                sendPrivateMessage(message.clientId(), "VIDEO_URL:%s".formatted(server.getRooms().get(message.roomId()).getVideoUrl()));
                broadcastMessage("JOINED_ROOM:%s".formatted(data[1]), message.roomId(), clientId);
                break;

            case "PLAY":
                if (message.clientId().equals(server.getRooms().get(message.roomId()).getHostId())) {
                    broadcastMessage("PLAY:%s".formatted(message.message()), message.roomId(), message.clientId());
                }
                break;
            case "CLOSE":
                if (message.clientId().equals(server.getRooms().get(message.roomId()).getHostId())) {
                    broadcastMessage("REMOVE_HOST_USER:%s".formatted(message.message()), message.roomId(), message.clientId());
                    removeAllRoomUsers(message.roomId());
                } else {
                    broadcastMessage("REMOVE_USER:%s".formatted(server.getClients().get(message.clientId()).clientName), message.roomId(), message.clientId());
                    removeUser(message.roomId(), message.clientId());
                }
                break;
            case "CHAT_MESSAGE":
                data = message.message().split(";", 2);
                if (data[1].length() > 50) {
                    sendPrivateMessage(message.clientId(), "CHAT_ERROR:%s"
                            .formatted(LocalizationUtil.getBundle().getString(BundleConstants.MESSAGE_LENGTH_MUST_BE)));
                    return;
                }
                broadcastMessage("CHAT_MESSAGE:%s:%s:%s".formatted(
                                server.getClients().get(message.clientId()).clientName,
                                data[0],
                                data[1]),
                        message.roomId());

                break;
            case "GET_ROOM_INFO":
                sendPrivateMessage(message.clientId(), "ROOM_INFO:%s-%s".formatted(
                        server.getRooms().get(message.roomId()).getCode(),
                        server.getRooms().get(message.roomId()).getPassword()));
                break;
            case "SET_ROOM_TITLE":
                if (message.message().length() < 40 && message.clientId().equals(server.getRooms().get(message.roomId()).getHostId())) {
                    server.getRooms().get(message.roomId()).setTitle(message.message());
                    broadcastMessage("CHANGE_TITLE:%s".formatted(message.message()),
                            message.roomId());
                    break;
                }
                sendPrivateMessage(message.clientId(), "CHAT_ERROR:%s"
                        .formatted(LocalizationUtil.getBundle().getString(BundleConstants.INSUFFICIENT_RIGHTS_TO_CHANGE_TITLE)));
                break;

            case "KICK": {

                if (message.clientId().equals(server.getRooms().get(message.roomId()).getHostId())) {
                    if (!message.message().equals("host")) {
                        Optional<ClientHandler> clientHandler =
                                server.getRooms().get(message.roomId()).getClients()
                                        .stream()
                                        .filter(client -> client.clientName.equals(message.message())).findAny();
                        if (clientHandler.isPresent()) {
                            sendPrivateMessage(clientHandler.get().clientId, "KICK_USER:nothing");
                            broadcastMessage("REMOVE_USER:%s".formatted(clientHandler.get().clientName), message.roomId());
                            removeUser(message.roomId(), clientHandler.get().getClientId());
                        }
                        break;

                    }
                    sendPrivateMessage(message.clientId(), "CHAT_ERROR:%s"
                            .formatted(LocalizationUtil.getBundle().getString(BundleConstants.CANNOT_KICK_HOST)));
                    break;
                }
                sendPrivateMessage(message.clientId(), "CHAT_ERROR:%s"
                        .formatted(LocalizationUtil.getBundle().getString(BundleConstants.INSUFFICIENT_RIGHTS_TO_KICK_USER)));
                break;

            }
            default:
                out.println("ERROR:UNKNOWN_COMMAND");
        }
    }

    private UserMessage getMessage(String request) {
        //type:clientId:roomId:data
        String[] data = request.split(":", 2);
        if (data.length > 1) {
            String[] messageData = data[1].split(":", 3);
            if (messageData.length == 3) {
                return new UserMessage(messageData[2], data[0], UUID.fromString(messageData[0]), messageData[1]);
            }
        }
        throw new RuntimeException();
    }

    public void broadcastMessage(String message, String roomId) {
        Room room = server.getRooms().get(roomId);
        for (UUID clientId : room.getClients().stream().map(ClientHandler::getClientId).collect(Collectors.toSet())) {
            server.sendMessage(clientId, message);
        }
    }

    public void broadcastMessage(String message, String roomId, UUID id) {
        for (UUID clientId : server.getRooms().get(roomId).getClients().stream().map(ClientHandler::getClientId).collect(Collectors.toSet())) {
            if (!clientId.equals(id)) server.sendMessage(clientId, message);
        }
    }


    private void sendPrivateMessage(UUID clientId, String message) {
        server.sendMessage(clientId, message);
    }

    private void stopClient() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client: " + e.getMessage());
        }
    }

    private void removeUser(String roomId, UUID clientId) {
        ClientHandler clientHandler = server.getClients().get(clientId);
        server.getClients().remove(clientId);
        server.getRooms().get(roomId).getClients().remove(clientHandler);
    }

    private void removeAllRoomUsers(String roomId) {
        for (ClientHandler client : server.getRooms().get(roomId).getClients()) {
            server.getClients().remove(client.clientId);
        }
        server.getRooms().remove(roomId);

    }

    public PrintWriter getOut() {
        return out;
    }

    public String getClientName() {
        return clientName;
    }

    public UUID getClientId() {
        return clientId;
    }
}