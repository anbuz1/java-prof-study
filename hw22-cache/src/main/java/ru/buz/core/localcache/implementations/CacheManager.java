package ru.buz.core.localcache.implementations;

import ru.buz.core.localcache.anotations.Cacheable;
import ru.buz.core.localcache.interfaces.BuzCache;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class CacheManager {

    private final BuzCache buzCache;
    private static CacheManager cacheManager;

    private CacheManager(Class<?>... aClass){
        this.buzCache = initializeBuzCache(aClass);
    }

    public static BuzCache getBuzCacheInstance(Class<?>... aClass){
        if(cacheManager==null){
            cacheManager = new CacheManager(aClass);
        }
        return cacheManager.buzCache;
    }

    private BuzCache initializeBuzCache(Class<?>... aClass){
        try {
        if(aClass.length != 0){
                return new BuzCacheImpl(aClass);
        }else {
            List<Class<?>> classList = new ArrayList<>();
            String paths = System.getProperty("java.class.path");
            for (String path : paths.split(";")) {
                classList.addAll(scanPackageAndFindMyFuckingClasses(path));
            }

            if(classList.size() != 0){
                return new BuzCacheImpl(aClass);
            }else {
                return new BuzCacheImpl();
            }
        }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Class<?>> scanPackageAndFindMyFuckingClasses(String path){
        List<Class<?>> classList = new ArrayList<>();
        String[] javas = path.split("main");
        String pack = javas[javas.length-1].replace("\\",".").substring(1);
        try (DirectoryStream<Path> pathStream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path path1 : pathStream) {
                if (Files.isDirectory(path1)){
                    classList.addAll(scanPackageAndFindMyFuckingClasses(path1.toString()));
                }
                else {
                    String name = path1.getFileName().toString();
                    if (name.contains(".class")) {
                        Class<?> aClass = null;
                        try {
                            String nameC = pack.concat(".").concat(name.replace(".class", ""));
                            aClass = Class.forName(nameC);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (aClass != null && isContainNeededAnnotation(aClass)){
                            classList.add(aClass);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }
    private static boolean isContainNeededAnnotation(Class<?> clazz) {
        return clazz.isAnnotationPresent(Cacheable.class);
    }

}
