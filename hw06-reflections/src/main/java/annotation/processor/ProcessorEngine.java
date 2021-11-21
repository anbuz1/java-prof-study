package annotation.processor;

import annotations.AfterTest;
import annotations.BeforeTest;
import annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public final class ProcessorEngine {

    private final Class<?> aClass;

    ProcessorEngine(Class<?> aClass) {
        this.aClass = aClass;
    }

    public List<Result> process() throws Exception {
        List<Result> resultList = new ArrayList<>();
        Map<AnnotationType, List<Method>> listMap = getMapWithSortedMethods();
        for (Method method : listMap.get(AnnotationType.TEST)) {
            Result result = new Result(method.getName(), getDescrFromMethodWithTestAnn(method), aClass.getName());
            Constructor<?> constructor = aClass.getConstructor();
            StringBuilder exceptions = new StringBuilder();
            Object object = constructor.newInstance();

            for (Method beforeMeth : listMap.get(AnnotationType.BEFORE)) {
                try {
                    beforeMeth.invoke(object);
                } catch (Exception e) {
                    exceptions.append("Exception in before stage: ").append(e.getCause()).append("\n");
                }
            }
            if (exceptions.length() == 0) {
                try {
                    method.invoke(object);
                } catch (Exception e) {
                    exceptions.append(e.getCause()).append("\n");
                }
                if (exceptions.length() == 0) {
                    result.setStatus(true);
                }

                for (Method afterMeth : listMap.get(AnnotationType.AFTER)) {
                    try {
                        afterMeth.invoke(object);
                    } catch (Exception e) {
                        exceptions.append("Exception in after stage: ").append(e.getCause()).append("\n");
                    }
                }
            }

            result.setException(exceptions.toString());

            resultList.add(result);

        }
        return resultList;
    }


    private Map<AnnotationType, List<Method>> getMapWithSortedMethods() {
        List<Method> testMethods = new ArrayList<>();
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        EnumMap<AnnotationType, List<Method>> listMap = new EnumMap<>(AnnotationType.class);

        for (Method method : aClass.getMethods()) {
            if (method.isAnnotationPresent(Test.class))
                testMethods.add(method);
            if (method.isAnnotationPresent(BeforeTest.class))
                beforeMethods.add(method);
            if (method.isAnnotationPresent(AfterTest.class))
                afterMethods.add(method);
        }

        listMap.put(AnnotationType.BEFORE, beforeMethods);
        listMap.put(AnnotationType.AFTER, afterMethods);
        listMap.put(AnnotationType.TEST, testMethods);

        return listMap;

    }


    private String getDescrFromMethodWithTestAnn(Method method) {
        if (method.isAnnotationPresent(Test.class)) {
            return method.getDeclaredAnnotation(Test.class).description();
        }
        return "";
    }

    private enum AnnotationType {
        BEFORE, AFTER, TEST
    }

}
