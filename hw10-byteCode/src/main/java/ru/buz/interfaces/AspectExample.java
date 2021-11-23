package ru.buz.interfaces;


import ru.buz.annotations.LogThis;

public interface AspectExample {
    String concat(String... strings);
    String concat(String str, int num);
    @LogThis
    String concat(String str, int... num);
}
