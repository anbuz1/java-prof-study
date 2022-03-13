package ru.buz.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.buz.api.SensorDataProcessor;
import ru.buz.api.model.SensorData;

public class SensorDataProcessorCommon implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorCommon.class);

    @Override
    public void process(SensorData data) {
        if (data.getValue() == null || data.getValue().isNaN()) {
            return;
        }
        log.info("Обработка данных: {}", data);
    }
}
