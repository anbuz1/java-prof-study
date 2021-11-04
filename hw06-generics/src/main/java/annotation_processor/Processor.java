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

public final class Processor {


    public static void start(String path) {
        List<Processing> processingList = new ArrayList<>();
        for (Class<?> aClass : scanPackage(path)) {
            if (isContainNeededAnnotation(aClass)) processingList.add(new Processing(aClass));
        }
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            List<Future<List<Result>>> futureList = executorService.invokeAll(processingList);
            futureList.forEach(r -> {
                try {
                    System.out.println("----------------------------------------------------------");
                    for (Result result : r.get()) {
                        System.out.println("______________________________________");
                        System.out.println("Test class: " + result.getClassName());
                        System.out.println("Test name: " + result.getMethodName());
                        System.out.println("Test description: " + result.getTestDescription());

                        if(result.isStatus()){
                            System.out.println("Test status: success");
                        }else{
                            System.out.println("Test status: fail");
                            System.out.println(result.getException());
                            System.out.println("______________________________________");
                        }

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

        for (Method method : clazz.getMethods()) {
            for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                if (declaredAnnotation.annotationType().getName().contains("anotations."))
                    return true;
            }
        }
        return false;
    }
}
