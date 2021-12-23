package ru.buz.dataprocessor;

import ru.buz.model.Measurement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        Map<String, Double> resultMap = new LinkedHashMap<>();
        for (Measurement measurement : data) {
            resultMap.merge(measurement.getName(), measurement.getValue(), Double::sum);
        }
        return resultMap;
    }
}
