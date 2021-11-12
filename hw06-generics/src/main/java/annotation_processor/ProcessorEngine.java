package annotation_processor;

import annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public final class ProcessorEngine implements Callable<List<Result>> {

    private final Class aClass;

    ProcessorEngine(Class aClass) {
        this.aClass = aClass;
    }

    @Override
    public List<Result> call() throws Exception {
        List<Result> resultList = new ArrayList<>();
        Map<String, List<Method>> listMap = getSortedMethods();
        for (Method method : listMap.get("Tests")) {
            Result result = new Result(method.getName(), getDescription(method), aClass.getName());
            Constructor constructor = aClass.getConstructor();
            StringBuilder exceptions = new StringBuilder();
            Object object = aClass.cast(constructor.newInstance());
            for (Method beforeMeth : listMap.get("Before")) {
                try {
                    beforeMeth.invoke(object);
                }catch (Exception e){
                    exceptions.append("Exception in before stage: ").append(e.getCause()).append("\n");
                }
            }

            try {
                method.invoke(object);
            }catch (Exception e){
                exceptions.append(e.getCause()).append("\n");
            }

            for (Method afterMeth : listMap.get("After")) {
                try {
                    afterMeth.invoke(object);
                }catch (Exception e){
                    exceptions.append("Exception in after stage: ").append(e.getCause()).append("\n");
                }
            }
            if(exceptions.length()==0)result.setStatus(true);

            result.setException(exceptions.toString());

            resultList.add(result);

        }
        return resultList;
    }

    private Map<String, List<Method>> getSortedMethods() {
        List<Method> testMethods = new ArrayList<>();
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        Map<String, List<Method>> listMap = new HashMap<>();

        for (Method method : aClass.getMethods()) {
            for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                if (declaredAnnotation.annotationType().getName().equals("annotations.Test"))
                    testMethods.add(method);
                if (declaredAnnotation.annotationType().getName().equals("annotations.BeforeTest"))
                    beforeMethods.add(method);
                if (declaredAnnotation.annotationType().getName().equals("annotations.AfterTest"))
                    afterMethods.add(method);
            }
        }

        listMap.put("Before", beforeMethods);
        listMap.put("After", afterMethods);
        listMap.put("Tests", testMethods);

        return listMap;

    }

    private String getDescription(Method method) {
        for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
            if (declaredAnnotation.annotationType().getName().equals("annotations.Test")) {
                Test test = (Test) declaredAnnotation;
                return test.description();
            }
        }
        return "";
    }

}
