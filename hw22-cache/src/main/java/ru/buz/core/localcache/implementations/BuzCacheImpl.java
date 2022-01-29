package ru.buz.core.localcache.implementations;

import ru.buz.core.localcache.anotations.CacheId;
import ru.buz.core.localcache.anotations.Cacheable;
import ru.buz.core.localcache.exceptions.PutInCacheException;
import ru.buz.core.localcache.interfaces.BuzCache;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class BuzCacheImpl implements BuzCache {

    private final Map<Class<?>, Method> methodMap;
    private final Map<Class<?>, Integer> mapSize;
    private final Map<Class<?>, Map<Field, Map<Object, List<Integer>>>> bigMap;
    private final List<WeakReference<?>> objectList;
    private Field idField;

    public BuzCacheImpl(Class<?>... aClass) {
        bigMap = new HashMap<>();
        methodMap = new HashMap<>();
        objectList = new ArrayList<>();
        mapSize = new HashMap<>();
        try {
            idField = BuzCacheImpl.class.getDeclaredField("idField");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        for (Class<?> clazz : aClass) {
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(CacheId.class)) {
                    methodMap.put(clazz, method);
                } else {
                    if (method.getName().equals("getId")) {
                        methodMap.put(clazz, method);
                    }
                }
            }
            bigMap.put(clazz, proceedObjectMap(clazz));
        }
    }


    @Override
    public void add(Object obj) throws PutInCacheException {
        add(null, obj);
    }


    @Override
    public void add(Object id, Object obj) throws PutInCacheException {
        Class<?> aClass = obj.getClass();
        Integer size = mapSize.get(aClass);
        if (id == null) {
            id = getObjectId(obj, aClass);
        }
        if (id == null) {
            throw new PutInCacheException("Can't find method to get id for: " + aClass.getName());
        }
        Optional<?> o = get(id, aClass);
        if (o.isPresent()) {
            update(id, o.get(), obj);
            size = size + 1;
            mapSize.put(aClass, size);
            return;
        }

        if (size == null) {
            addObject(aClass, obj, id);
        } else {
            if (size > 0) {
                size = size - 1;
                mapSize.put(aClass, size);
                addObject(aClass, obj, id);
            } else {
                throw new PutInCacheException("Reached limit cache size");
            }
        }
    }


    @Override
    public <T> Optional<T> get(Object id, Class<T> aClass) {
        Integer index = getIndex(id, aClass);
        if (index != null) {
            WeakReference<?> reference = objectList.get(index);
            T result = (T) reference.get();
            if (result == null) {
                return Optional.empty();
            }
            return Optional.of(result);
        }
        return Optional.empty();
    }

    @Override
    public <T> List<T> get(String field, Object value, Class<T> aClass) {
        Field declaredField;
        try {
            declaredField = aClass.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return get(declaredField, value, aClass);

    }

    @Override
    public void delete(Object id, Class<?> aClass) {
        Map<Field, Map<Object, List<Integer>>> fieldMapMap = bigMap.get(aClass);
        Integer index = getIndex(id, aClass);
        fieldMapMap.values().forEach(map -> map.values().forEach(list -> list.remove(index)));
        fieldMapMap.values().forEach(map -> map.values().removeIf(list -> list.size() == 0));
        Integer size = mapSize.get(aClass);
        size = size + 1;
        mapSize.put(aClass, size);
    }

    @Override
    public void delete(Object obj) {
        Class<?> aClass = obj.getClass();
        Object id = getObjectId(obj, aClass);
        if (id != null) {
            delete(id, aClass);
        }
    }

    @Override
    public void clearCache() {
        if (objectList.size() != 0) {
            objectList.clear();
            bigMap.values().forEach(map -> map.values().forEach(Map::clear));
            bigMap.values().forEach(Map::clear);
            mapSize.clear();
            bigMap.replaceAll((c, v) -> proceedObjectMap(c));
        }
    }

    @Override
    public int size(Class<?> tClass) {
        return bigMap.get(tClass).get(idField).size();
    }

    @Override
    public int size() {
        return objectList.size();
    }

    private Map<Field, Map<Object, List<Integer>>> proceedObjectMap(Class<?> aClass) {
        Map<Field, Map<Object, List<Integer>>> fieldMap = new HashMap<>();
        for (Field field : aClass.getDeclaredFields()) {
            Map<Object, List<Integer>> linkMap = new HashMap<>();
            fieldMap.put(field, linkMap);
        }
        if (aClass.isAnnotationPresent(Cacheable.class)) {
            int i = aClass.getDeclaredAnnotation(Cacheable.class).cacheSize();
            mapSize.put(aClass, i);
        }
        fieldMap.put(idField, new HashMap<>());
        return fieldMap;
    }

    private void addObject(Class<?> aClass, Object obj, Object id) throws PutInCacheException {
        WeakReference<Object> weakReference = new WeakReference<>(obj);
        objectList.add(weakReference);
        int objectIndex = objectList.size() - 1;
        Map<Field, Map<Object, List<Integer>>> objectMap = bigMap.computeIfAbsent(aClass, key -> proceedObjectMap(aClass));
        objectMap.get(idField).put(id, new ArrayList<>(Arrays.asList(objectIndex)));
        for (Field field : aClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object key = field.get(obj);
                List<Integer> values = objectMap.get(field).computeIfAbsent(key, k -> new ArrayList<>());
                values.add(objectIndex);
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    private Object getObjectId(Object o, Class<?> aClass) {
        Method methodForId = methodMap.get(aClass);
        if (methodForId != null) {
            try {
                return methodForId.invoke(o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private <T> List<T> get(Field field, Object value, Class<T> aClass) {

        List<T> resultList = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Integer> indexList = bigMap.get(aClass).get(field).get(value);
        if (indexList == null) {
            return resultList;
        }
        indices.addAll(indexList);

        for (Integer index : indices) {
            WeakReference<?> reference;
            T result = null;
            if ((reference = objectList.get(index)) != null) {
                result = (T) reference.get();
                resultList.add(result);
            }
        }
        return resultList;
    }

    private void update(Object id, Object oldObj, Object newObj) throws PutInCacheException {
        Class<?> aClass = oldObj.getClass();
        Integer index = getIndex(id, aClass);
        objectList.set(index, null);
        addObject(aClass, newObj, id);
    }

    private Integer getIndex(Object id, Class<?> aClass) {
        Map<Field, Map<Object, List<Integer>>> fieldMapMap = bigMap.get(aClass);
        Map<Object, List<Integer>> objectListMap = fieldMapMap.get(idField);
        List<Integer> indices;
        if ((indices = objectListMap.get(id)) == null) {
            if (id instanceof Integer) {
                Long x = Long.valueOf((Integer) id);
                if ((indices = objectListMap.get(x)) == null) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return indices.get(0);
    }

//    private void delete(Integer index, Class<?> aClass) {
//        Map<Field, Map<Object, List<Integer>>> fieldMapMap = bigMap.get(aClass);
//        fieldMapMap.values().forEach(map -> map.values().forEach(list -> list.remove(index)));
//        fieldMapMap.values().forEach(map -> map.values().removeIf(list -> list.size() == 0));
//        Integer size = mapSize.get(aClass);
//        size = size + 1;
//        mapSize.put(aClass, size);
//    }

}
