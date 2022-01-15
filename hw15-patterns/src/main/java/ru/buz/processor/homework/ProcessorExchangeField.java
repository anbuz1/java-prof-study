package ru.buz.processor.homework;

import ru.buz.model.Message;
import ru.buz.processor.Processor;

public class ProcessorExchangeField implements Processor {
    @Override
    public Message process(Message message) {
        return message.toBuilder().field11(message.getField12()).field12(message.getField11()).build();
    }
}
