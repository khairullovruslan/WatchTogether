package ru.itis.khairullovruslan.watchtogether.client;

import ru.itis.khairullovruslan.watchtogether.model.net.Message;
import ru.itis.khairullovruslan.watchtogether.util.RandomColorGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;


public class WatchTogetherClient {

    private final String host;
    private final int port;

    private String roomCode;


    private final UUID clientId;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String messageColor;


    private MessageReceiverController controller;
    private MessageReceiverController videoPlayerController;

    public WatchTogetherClient(String host, int port, UUID clientId) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.messageColor = RandomColorGenerator.generate();
    }


    public void connect() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connected to server at " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }


    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void listenForResponses() {
        new Thread(() -> {
            try {
                String response;
                while (socket != null && !socket.isClosed() && (response = in.readLine()) != null) {
                    String[] responseData = response.split(":", 2);
                    System.out.println(Arrays.toString(responseData));
                    if (responseData.length < 2) {
                        throw new RuntimeException();
                    }
                    if (videoPlayerController != null) {
                        videoPlayerController.receiveMessage(new Message(responseData[0], responseData[1]));
                    }
                    controller.receiveMessage(new Message(responseData[0], responseData[1]));
                }
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.err.println("Error while reading response: " + e.getMessage());
                }
            }
        }).start();

    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setController(MessageReceiverController controller) {
        this.controller = controller;
    }

    public void setVideoController(MessageReceiverController controller) {
        this.videoPlayerController = controller;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getMessageColor() {
        return messageColor;
    }
}
