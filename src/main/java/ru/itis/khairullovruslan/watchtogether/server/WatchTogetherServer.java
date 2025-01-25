package ru.itis.khairullovruslan.watchtogether.server;


import ru.itis.khairullovruslan.watchtogether.constants.NetworkConstants;
import ru.itis.khairullovruslan.watchtogether.model.net.Room;
import ru.itis.khairullovruslan.watchtogether.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class WatchTogetherServer {


    private final int port;

    private ServerSocket serverSocket;
    private boolean isAlive = true;
    private static final Map<UUID, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public WatchTogetherServer(int port) {
        this.port = port;
    }


    public static void main(String[] args) {
        WatchTogetherServer server = new WatchTogetherServer(NetworkConstants.PORT);
        server.start();
    }


    public void start() {
        try {
            System.out.println("start server");
            serverSocket = new ServerSocket(port);

            while (isAlive) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
        }
    }

    public void addRoom(Room room){
        rooms.put(room.getCode(), room);
    }

    public Map<String, Room> getRooms(){
        return rooms;
    }
    public Map<UUID, ClientHandler> getClients(){
        return clients;
    }

    public void addClient(UUID uuid, ClientHandler client){
        clients.put(uuid, client);
    }

    public void removeClient(UUID uuid){
        clients.remove(uuid);
    }
    public void roomAddClient(String code, ClientHandler clientHandler){
        rooms.get(code).getClients().add(clientHandler);
    }

    public void close() {
        try {
            isAlive = false;
            if (serverSocket != null) {
                serverSocket.close();
            }
            System.out.println("Server is shutting down...");
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    public void sendMessage(UUID clientId, String message) {
        ClientHandler client = clients.get(clientId);
        client.getOut().println(message);
        client.getOut().flush();
    }

}
