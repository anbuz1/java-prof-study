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
    private final Field idField;

    public BuzCacheImpl(Class<?>... aClass) throws NoSuchFieldException {
        bigMap = new HashMap<>();
        methodMap = new HashMap<>();
        objectList = new ArrayList<>();
        mapSize = new HashMap<>();
        idField = BuzCacheImpl.class.getDeclaredField("idField");
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

    private BuzCacheImpl() throws NoSuchFieldException {
        this(new Class[0]);
    }


    @Override
    public void add(Object obj) throws PutInCacheException {
        add(-1, obj);
    }


    @Override
    public void add(long id, Object obj) throws PutInCacheException {
        Class<?> aClass = obj.getClass();
        if(id<=0){
            id = getObjectId(obj, aClass);
        }
        if(id<=0){
            throw new PutInCacheException("Can't find method to get id for: " + aClass.getName());
        }
        Optional<?> o = get(id, aClass);
        if(o.isPresent()){
            delete(id,aClass);
        }
        Integer size = mapSize.get(aClass);
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
    public <T> Optional<T> get(long id, Class<T> aClass) {

        Map<Field, Map<Object, List<Integer>>> fieldMapMap = bigMap.get(aClass);
        Map<Object, List<Integer>> objectIntegerMap = fieldMapMap.get(idField);
        List<Integer> indices = objectIntegerMap.get(id);
        if(indices != null){
            WeakReference<?> reference = objectList.get(indices.get(0));
            T result = (T) reference.get();
            if(result == null){
                delete(id,aClass);
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
        return get(declaredField,value,aClass);
    }

    @Override
    public void delete(long id, Class<?> aClass) {
        Map<Field, Map<Object, List<Integer>>> fieldMapMap = bigMap.get(aClass);
        Map<Object, List<Integer>> objectListMap = fieldMapMap.get(idField);
        List<Integer> indices = objectListMap.get(id);
        Integer index = indices.get(0);
        fieldMapMap.values().forEach(map -> map.values().forEach(list -> list.remove(index)));
        fieldMapMap.values().forEach(map -> map.values().removeIf(list ->list.size()==0));
        Integer size = mapSize.get(aClass);
        size = size + 1;
        mapSize.put(aClass, size);
    }
    private void delete(Integer index, Class<?> aClass) {
        Map<Field, Map<Object, List<Integer>>> fieldMapMap = bigMap.get(aClass);
        fieldMapMap.values().forEach(map -> map.values().forEach(list -> list.remove(index)));
        fieldMapMap.values().forEach(map -> map.values().removeIf(list ->list.size()==0));
        Integer size = mapSize.get(aClass);
        size = size + 1;
        mapSize.put(aClass, size);
    }

    @Override
    public void delete(Object obj) {
        Class<?> aClass = obj.getClass();
        long id = getObjectId(obj, aClass);
        if(id>0){
            delete(id,aClass);
        }
    }

    @Override
    public void clearCache(){
        if (objectList.size()!=0){
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

    private void addObject(Class<?> aClass, Object obj, long id) throws PutInCacheException {
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

    private long getObjectId(Object o, Class<?> aClass) {
        Method methodForId = methodMap.get(aClass);
        if (methodForId != null) {
            try {
                return (long) methodForId.invoke(o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private  <T> List<T> get(Field field, Object value, Class<T> aClass) {

            List<T> resultList = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
                indices.addAll(bigMap.get(aClass).get(field).get(value));

        for (Integer index : indices) {
            WeakReference<?> reference = objectList.get(index);
            T result = (T) reference.get();
            if(result == null){
                delete(index,aClass);
            }
            resultList.add(result);
        }
        return resultList;
    }


}
