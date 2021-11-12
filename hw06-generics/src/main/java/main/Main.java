package main;

import static annotation_processor.AnnotationProcessor.start;

public class Main {
    public static void main(String[] args) {

        start(System.getProperty("java.class.path").split(";")[0]);

    }
}
