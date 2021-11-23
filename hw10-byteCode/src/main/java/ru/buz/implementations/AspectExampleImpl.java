package ru.buz.implementations;

import ru.buz.annotations.LogThis;
import ru.buz.interfaces.AspectExample;

public class AspectExampleImpl implements AspectExample {

    @Override
    public String concat(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string);
        }
        return sb.toString();
    }

    @LogThis
    public String concat(String str, int num) {
        return str + num;
    }

    public String concat(String str, int... num) {
        StringBuilder sb = new StringBuilder(str);
        for (int i : num) {
            sb.append(i);
        }
        return sb.toString();
    }

}
