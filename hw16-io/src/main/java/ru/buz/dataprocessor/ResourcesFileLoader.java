package ru.buz.dataprocessor;

import ru.buz.model.Measurement;
import javax.json.*;
import java.util.*;


public class ResourcesFileLoader implements Loader {

    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        List<Measurement> result = new ArrayList<>();
        try (var jsonReader = Json.createReader(ResourcesFileLoader.class.getClassLoader().getResourceAsStream(fileName))) {
            JsonStructure jsonFromTheFile = jsonReader.read();
            for (JsonValue jsonValue : jsonFromTheFile.asJsonArray()) {
                JsonObject jsonObject = jsonValue.asJsonObject();
                result.add(new Measurement(jsonObject.get("name").toString()
                        ,Double.parseDouble(jsonObject.get("value").toString())));
            }
        }
        return result;
    }
}
