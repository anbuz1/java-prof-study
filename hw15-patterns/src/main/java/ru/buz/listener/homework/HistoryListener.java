package ru.buz.listener.homework;

import ru.buz.listener.Listener;
import ru.buz.model.Message;

import java.util.*;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Deque<Message>> mapMessage = new TreeMap<>();

    @Override
    public void onUpdated(Message msg) {
        Message copyMessage = new Message(msg);
        mapMessage.computeIfAbsent(copyMessage.getId(), k -> new ArrayDeque<>()).add(copyMessage);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(mapMessage.get(id).poll());
    }
}
