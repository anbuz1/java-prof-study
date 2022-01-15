package ru.buz.handler;

import ru.buz.model.Message;
import ru.buz.listener.Listener;

public interface Handler {
    Message handle(Message msg);

    void addListener(Listener listener);
    void removeListener(Listener listener);
}
