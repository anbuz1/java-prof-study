package ru.buz.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class FileSerializer implements Serializer {

    private final String fileName;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String json = objectMapper.writeValueAsString(data);
            json = json.replace("\\\"","");
//            objectMapper.writeValue(new File(fileName),json);
            Files.writeString(Paths.get(fileName), json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        //формирует результирующий json и сохраняет его в файл
    }
}
