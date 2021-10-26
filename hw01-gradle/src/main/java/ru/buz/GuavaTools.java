package ru.buz;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import java.util.List;

public class GuavaTools <T>{

    List<T> removeNullFromList(List<T> list){
        if (Iterables.removeIf(list, Predicates.isNull()))
        return list;
        else return null;
    }

}
