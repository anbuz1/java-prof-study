package main;

import annotation.processor.AnnotationProcessor;

public class Main {
    public static void main(String[] args) {

        AnnotationProcessor annotationProcessor= new AnnotationProcessor();
        //line 10 only for demonstration
        if(args.length==0){annotationProcessor.start("tests.Check");}

        for (String arg : args) {
            if(arg!=null){
                annotationProcessor.start(arg);
            }
        }
    }
}
