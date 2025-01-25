package ru.itis.khairullovruslan.watchtogether.server;


import java.util.UUID;

public record UserMessage(String message, String type, UUID clientId, String roomId) {
}

