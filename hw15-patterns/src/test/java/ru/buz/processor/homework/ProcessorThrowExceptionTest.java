package ru.buz.processor.homework;

import org.junit.jupiter.api.Test;
import ru.buz.model.Message;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProcessorThrowExceptionTest {

    ProcessorThrowException processorThrowException = new ProcessorThrowException();

    @Test
    void handleProcessorsTest(){
        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .field11("field11")
                .field12("field12")
                .build();
        int sec;

        for (int i = 0; i < 200000; i++) {
            int count = 0;
            do{
                count++;
                sec = LocalDateTime.now().getSecond() % 2;
                if(sec != 0){
                    assertThrows(RuntimeException.class,() -> processorThrowException.process(message));
                }

            }while (sec != 0);
            System.out.println("Count = " + count);

        }
    }

}