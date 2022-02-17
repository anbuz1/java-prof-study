package ru.buz.core.localcache.implementations;

import ru.buz.core.localcache.exceptions.PutInCacheException;
import ru.buz.core.localcache.interfaces.BuzCache;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleBuzCacheImpl implements BuzCache {

    private final Map<Class<?>, Map<Object, Object>> bigMap;

    public SimpleBuzCacheImpl(Class<?>... aClass) {
        bigMap = new HashMap<>();
        for (Class<?> aClass1 : aClass) {
            bigMap.put(aClass1, new WeakHashMap<>());
        }
    }

    private SimpleBuzCacheImpl() {
        bigMap = null;
    }

    @Override
    public <T> void add(T obj) throws PutInCacheException {
        throw new PutInCacheException("can't add without ID! Try use another add method");
    }

    @Override
    public <T> void add(Object id, T obj) throws PutInCacheException {
        Class<T> aClass = (Class<T>) obj.getClass();
        if (bigMap.computeIfPresent(aClass, (key, map) -> {
            map.put(id, obj);
            return map;
        }) == null) {
            throw new PutInCacheException("you try object which type is not presented");
        }
    }


    @Override
    public <T> Optional<T> get(Object key, Class<T> tClass) {
        Object o;
        if ((o = bigMap.get(tClass).get(key)) == null) {
            if (key instanceof Integer) {
                Long x = Long.valueOf((Integer) key);
                o = bigMap.get(tClass).get(x);
            } else {
                try {
                    Object castObj = bigMap.get(tClass).keySet().stream().findFirst().get();
                    Object truKey = castObj.getClass().cast(key);
                    o = bigMap.get(tClass).get(truKey);
                } catch (ClassCastException | NullPointerException ex) {
                }
            }
        }
        return Optional.ofNullable((T) o);
    }

    @Override
    public <T> List<T> get(String field, Object value, Class<T> aClass) {
        Map<Object, T> objectMap = (Map<Object, T>) bigMap.get(aClass);
        List<T> collect = objectMap.values().stream().filter(obj -> {
            for (Field declaredField : obj.getClass().getDeclaredFields()) {
                if (declaredField.getName().equals(field)) {
                    declaredField.setAccessible(true);
                    Object key = null;
                    try {
                        key = declaredField.get(obj);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return value.equals(key);
                }
            }
            return false;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void delete(Object id, Class<?> aClass) {
        if (bigMap.get(aClass).get(id) == null) {
            if (id instanceof Integer) {
                Long key = Long.valueOf((Integer) id);
                if (bigMap.get(aClass).get(key) != null) {
                    bigMap.get(aClass).remove(key);
                }
            } else {
                try {
                    Object castObj = bigMap.get(aClass).keySet().stream().findFirst().get();
                    Object truKey = castObj.getClass().cast(id);
                    bigMap.get(aClass).remove(truKey);
                } catch (ClassCastException | NullPointerException ex) {
                }

            }
        } else {
            bigMap.get(aClass).remove(id);
        }
    }

    @Override
    public void delete(Object obj) {

    }

    @Override
    public void clearCache() {
        bigMap.values().forEach(Map::clear);
    }

    @Override
    public int size(Class<?> tClass) {
        return bigMap.get(tClass).keySet().size();
    }

    @Override
    public int size() {
        return 0;
    }
}
