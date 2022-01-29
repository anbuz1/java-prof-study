package ru.buz.core.localcache.interfaces;

import ru.buz.core.localcache.exceptions.PutInCacheException;

import java.util.List;
import java.util.Optional;

public interface BuzCache {

    <T> void add(T obj) throws PutInCacheException;


    <T> void add(Object id, T obj) throws PutInCacheException;

    <T> Optional<T> get(Object key, Class<T> tClass);

    <T> List<T> get(String field, Object value, Class<T> aClass);

    void delete (Object id, Class<?> aClass);

    void delete (Object obj);

    void clearCache();

    int size(Class<?> tClass);

    int size();

}
