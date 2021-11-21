package annotation.processor;

import java.util.List;

public final class AnnotationProcessor {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";


    public void start(String className) {

        try {
            ProcessorEngine processorEngine = new ProcessorEngine(Class.forName(className));
            List<Result> futureList = processorEngine.process();
            System.out.println("----------------------------------------------------------");
            futureList.forEach(AnnotationProcessor::printResult);
            System.out.println("----------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void printResult(Result r) {

        System.out.println("______________________________________");
        System.out.println(ANSI_BLUE + "Test class: " + ANSI_RESET + r.getClassName());
        System.out.println(ANSI_BLUE + "Test name: " + ANSI_RESET + r.getMethodName());
        System.out.println(ANSI_BLUE + "Test description: " + ANSI_RESET + r.getTestDescription());

        if (r.isStatus()) {
            System.out.println(ANSI_YELLOW + "Test status: " + ANSI_GREEN + "success" + ANSI_RESET);
        } else {
            System.out.println(ANSI_YELLOW + "Test status: " + ANSI_RED + "fail" + ANSI_RESET);
            System.out.print(ANSI_BLUE + "Exception in test: " + ANSI_RED + r.getException() + ANSI_RESET);
        }
        System.out.println("______________________________________");

    }
}
