package ru.itis.khairullovruslan.watchtogether.client;

import ru.itis.khairullovruslan.watchtogether.model.net.Message;

public interface Client {
    void connect();

    void sendMessage(Message message);
}
