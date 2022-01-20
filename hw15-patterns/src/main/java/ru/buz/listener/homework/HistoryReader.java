package ru.buz.listener.homework;

import ru.buz.model.Message;

import java.util.Optional;

public interface HistoryReader {

    Optional<Message> findMessageById(long id);
}
