package main;

import static annotation.processor.AnnotationProcessor.start;

public class Main {
    public static void main(String[] args) {
        //line 8 only for demonstration
        if(args.length==0){start("tests.Check");}

        for (String arg : args) {
            if(arg!=null){
                start(arg);
            }
        }
    }
}
