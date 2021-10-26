package ru.buz;

import com.google.common.collect.Iterables;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        if (args.length > 0) {
            for (String arg : args) {
                System.out.println(arg);
            }
            System.out.println("size: " + Iterables.size(Arrays.asList(args)));
        }
        else System.out.println("needs arguments");
    }
}
