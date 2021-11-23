package ru.buz;

import ru.buz.implementations.AspectExampleImpl;
import ru.buz.interfaces.AspectExample;

public class Main {
    public static void main(String[] args) {
        AspectExample ae = AOPTools.getInstance(AspectExampleImpl.class);

        assert ae != null;
        System.out.println(ae.concat("first", "second"));
        System.out.println("===================================================");
        System.out.println(ae.concat("first", 5));
        System.out.println("===================================================");
        System.out.println(ae.concat("first", 5,6,7));
    }
}
