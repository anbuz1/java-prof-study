package ru.buz.core.localcache.interfaces;

import ru.buz.core.localcache.exceptions.PutInCacheException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public interface BuzCache {

    <T> void add(T obj) throws PutInCacheException;

    <T>void add(long id, T obj) throws PutInCacheException;

    <T> Optional<T> get(long key, Class<T> tClass);

    <T> List<T> get(String field, Object value, Class<T> aClass);

    void delete (long id, Class<?> aClass);

    void delete (Object obj);

    void clearCache();

    int size(Class<?> tClass);

    int size();

}
