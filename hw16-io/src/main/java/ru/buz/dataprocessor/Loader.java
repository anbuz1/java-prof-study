package ru.buz.dataprocessor;

import ru.buz.model.Measurement;

import java.util.List;

public interface Loader {

    List<Measurement> load();
}
