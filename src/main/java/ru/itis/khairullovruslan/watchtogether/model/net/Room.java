package ru.itis.khairullovruslan.watchtogether.model.net;

import ru.itis.khairullovruslan.watchtogether.server.WatchTogetherServer;
import ru.itis.khairullovruslan.watchtogether.server.handler.ClientHandler;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private String code;
    private String password;
    private UUID hostId;
    private String title = "Без названия";
    private Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private String videoUrl;

    public Room(String code, String password, UUID hostId, String videoUrl) {
        this.code = code;
        this.password = password;
        this.hostId = hostId;
        this.videoUrl = videoUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getHostId() {
        return hostId;
    }

    public void setHostId(UUID hostId) {
        this.hostId = hostId;
    }

    public Set<ClientHandler> getClients() {
        return clients;
    }

    public void setClients(Set<ClientHandler> clients) {
        this.clients = clients;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
