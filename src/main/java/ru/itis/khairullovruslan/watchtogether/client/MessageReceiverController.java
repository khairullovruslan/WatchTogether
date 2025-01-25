package ru.itis.khairullovruslan.watchtogether.client;

import ru.itis.khairullovruslan.watchtogether.controllers.base.Controller;
import ru.itis.khairullovruslan.watchtogether.model.net.Message;

public interface MessageReceiverController{
    void receiveMessage(Message message);
}
