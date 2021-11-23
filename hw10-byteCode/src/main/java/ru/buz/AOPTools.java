package ru.buz;


import ru.buz.annotations.LogThis;

import java.lang.reflect.*;
import java.util.*;

public abstract class AOPTools {

    private static final Map<String, List<Class<?>[]>> mapOfMethods = new HashMap<>();

    static <T> T getInstance(Class<? super T> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        Object ob;
        List<Class<?>[]> parametersList = new ArrayList<>();
        if (interfaces.length == 0) {
            return null;
        }
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(LogThis.class)) {
                if (!mapOfMethods.containsKey(method.getName())) {
                    mapOfMethods.put(method.getName(), parametersList);
                }
                parametersList.add(method.getParameterTypes());
            }
        }

        try {
            Constructor<?> constructor = clazz.getConstructor();
            ob = constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
        InvocationHandler handler = new MyInvocationHandler<>(ob);

        return (T) Proxy.newProxyInstance(AOPTools.class.getClassLoader(),
                new Class<?>[]{interfaces[0]}, handler);

    }

    static class MyInvocationHandler<T> implements InvocationHandler {
        private final T instance;

        MyInvocationHandler(T instance) {
            this.instance = instance;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //If Annotation on interface method
            if (method.isAnnotationPresent(LogThis.class)) {
                printAspect(method);
            } else if (isAnnotatedMethod(method)) {//If annotation on instance method
                printAspect(method);
            }


            return method.invoke(instance, args);
        }

        private boolean isAnnotatedMethod(Method method) {
            List<Class<?>[]> listParams = mapOfMethods.get(method.getName());
            for (Class<?>[] listParam : listParams) {
                if (Arrays.asList(listParam).containsAll(Arrays.asList(method.getParameterTypes()))) {
                    return true;
                }
            }
            return false;
        }

        private void printAspect(Method method) {
            System.out.println("---------------------------------");
            System.out.println("invoking method:" + method);
            System.out.println("---------------------------------");

        }
    }

}
