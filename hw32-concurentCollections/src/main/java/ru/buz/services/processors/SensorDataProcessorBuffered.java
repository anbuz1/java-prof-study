package ru.buz.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.buz.api.SensorDataProcessor;
import ru.buz.api.model.SensorData;
import ru.buz.lib.SensorDataBufferedWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// Этот класс нужно реализовать
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final BlockingQueue<SensorData> dataBuffer;
    private final List<SensorData> bufferedData;
    private final Comparator<SensorData> sensorDataComparator;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.bufferedData = Collections.synchronizedList(new ArrayList<SensorData>());
        this.dataBuffer = new LinkedBlockingQueue<>();
        this.sensorDataComparator = Comparator.comparing(SensorData::getMeasurementTime);
    }

    @Override
    public void process(SensorData data) {
        dataBuffer.add(data);
        if (dataBuffer.size() >= bufferSize) {
            flush();
        }

    }

    public synchronized void flush() {
        bufferedData.clear();
        while (dataBuffer.size() != 0) {
            SensorData poll = dataBuffer.poll();
            bufferedData.add(poll);
        }
        if (bufferedData.size() > 0) {
            bufferedData.sort(sensorDataComparator);
            try {
                    writer.writeBufferedData(bufferedData);
            } catch (Exception e) {
                log.error("Ошибка в процессе записи буфера", e);
            }
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
