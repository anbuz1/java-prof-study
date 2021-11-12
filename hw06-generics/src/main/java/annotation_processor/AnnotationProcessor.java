package annotation_processor;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class AnnotationProcessor {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";


    public static void start(String path) {
        List<ProcessorEngine> processorEngineList = new ArrayList<>();
        for (Class<?> aClass : scanPackage(path)) {
            if (isContainNeededAnnotation(aClass)) processorEngineList.add(new ProcessorEngine(aClass));
        }
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            List<Future<List<Result>>> futureList = executorService.invokeAll(processorEngineList);
            futureList.forEach(r -> {
                try {
                    System.out.println("----------------------------------------------------------");
                    for (Result result : r.get()) {
                        System.out.println("______________________________________");
                        System.out.println(ANSI_BLUE + "Test class: " + ANSI_RESET + result.getClassName());
                        System.out.println(ANSI_BLUE + "Test name: " + ANSI_RESET + result.getMethodName());
                        System.out.println(ANSI_BLUE + "Test description: " + ANSI_RESET + result.getTestDescription());

                        if(result.isStatus()){
                            System.out.println(ANSI_YELLOW + "Test status: " + ANSI_GREEN + "success" + ANSI_RESET);
                        }else{
                            System.out.println(ANSI_YELLOW + "Test status: " + ANSI_RED + "fail" + ANSI_RESET);
                            System.out.print(ANSI_BLUE + "Exception in test: " + ANSI_RED + result.getException() + ANSI_RESET);
                        }
                        System.out.println("______________________________________");

                    }
                    System.out.println("----------------------------------------------------------");

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<Class<?>> scanPackage(String path) {
        List<Class<?>> classList = new ArrayList<>();
        String pack = new File(path).getName();
        try (DirectoryStream<Path> pathStream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path path1 : pathStream) {
                if (Files.isDirectory(path1)) classList.addAll(scanPackage(path1.toString()));
                else {
                    String name = path1.getFileName().toString();
                    if (name.contains(".class")) {
                        Class<?> aClass = null;
                        try {
                            aClass = Class.forName(pack.concat(".").concat(name.replace(".class", "")));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (aClass != null) classList.add(aClass);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }

    private static boolean isContainNeededAnnotation(Class<?> clazz) {
        if(clazz.isAnnotationPresent(annotations.Test.class))return true;

        for (Method method : clazz.getMethods()) {
            for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                if (declaredAnnotation.annotationType().getName().contains("annotations."))
                    return true;
            }
        }
        return false;
    }
}
