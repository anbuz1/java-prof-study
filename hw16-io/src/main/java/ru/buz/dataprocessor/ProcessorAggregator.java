package ru.buz.dataprocessor;

import ru.buz.model.Measurement;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        Map<String, Double> resultMap = new TreeMap<>();
        for (Measurement measurement : data) {
            resultMap.merge(measurement.getName(), measurement.getValue(), Double::sum);
        }
        return resultMap;
    }
}
