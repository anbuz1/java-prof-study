package ru.buz.appcontainer;

import ru.buz.appcontainer.api.AppComponent;
import ru.buz.appcontainer.api.AppComponentsContainer;
import ru.buz.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<ComponentHolder> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        initialize(configClass);
    }

    private void initialize(Class<?> aClass) {
        Object mainObject;
        try {
            Constructor<?> constructor = aClass.getConstructor();
            mainObject = constructor.newInstance();

            for (Method declaredMethod : aClass.getDeclaredMethods()) {
                if (declaredMethod.isAnnotationPresent(AppComponent.class)) {
                    String name = declaredMethod.getDeclaredAnnotation(AppComponent.class).name();
                    int order = declaredMethod.getDeclaredAnnotation(AppComponent.class).order();

                    appComponents.add(new ComponentHolder(order, name, declaredMethod, mainObject, this));
                }
            }
            appComponents.sort(Comparator.comparingInt(ComponentHolder::getOrder));
            appComponents.forEach(ComponentHolder::initializeObject);
            appComponents.forEach(c -> appComponentsByName.put(c.getName(), c.getComponent()));

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        for (ComponentHolder appComponent : appComponents) {
            Class<?> type = appComponent.getType();
            if (type.equals(componentClass) || componentClass.isAssignableFrom(type)) {
                return (C) appComponent.getComponent();
            }
        }
        return null;
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return (C) appComponentsByName.get(componentName);
    }

    private static class ComponentHolder {
        private final int order;
        private final String name;
        private final Method method;
        private final Object mainObject;
        private final AppComponentsContainer container;
        private Object component;
        private Class<?> aClass = this.getClass();

        public ComponentHolder(int order, String name, Method method, Object mainObject, AppComponentsContainer container) {
            this.order = order;
            this.method = method;
            this.name = name;
            this.mainObject = mainObject;
            this.container = container;
        }

        private void initializeObject() {
            try {
                Object[] parameters;
                Class<?>[] parameterTypes = method.getParameterTypes();
                int length = parameterTypes.length;
                if (length > 0) {
                    parameters = new Object[length];
                    for (int i = 0; i < length; i++) {
                        parameters[i] = container.getAppComponent(parameterTypes[i]);
                    }
                    component = method.invoke(mainObject, parameters);
                } else {
                    component = method.invoke(mainObject);
                }
                aClass = component.getClass();

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public int getOrder() {
            return order;
        }

        public String getName() {
            return name;
        }

        public Object getComponent() {
            return component;
        }

        public Class<?> getType() {
            return aClass;
        }
    }
}
