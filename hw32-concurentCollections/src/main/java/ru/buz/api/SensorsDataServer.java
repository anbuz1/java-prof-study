package ru.buz.api;

import ru.buz.api.model.SensorData;

public interface SensorsDataServer {
    void onReceive(SensorData sensorData);
}
