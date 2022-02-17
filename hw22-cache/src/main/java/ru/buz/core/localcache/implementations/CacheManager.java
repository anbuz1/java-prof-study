package ru.buz.core.localcache.implementations;

import ru.buz.core.localcache.anotations.Cacheable;
import ru.buz.core.localcache.interfaces.BuzCache;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CacheManager {


    private final Map<String, BuzCache> instanceHolder;
    private final static CacheManager cacheManager = new CacheManager();

    private CacheManager() {

        instanceHolder = new HashMap<>();
    }

    public static BuzCache getBuzCacheInstance(Class<?>... aClass) {

        return cacheManager.initializeBuzCache(aClass);
    }

    private BuzCache initializeBuzCache(Class<?>... aClass) {

        if (aClass.length != 0) {
            StringBuilder key = new StringBuilder();
            for (Class<?> aClass1 : aClass) {
                key.append(aClass1.getName());
            }
            return instanceHolder.computeIfAbsent(key.toString(), (k) -> new BuzCacheImpl(aClass));
        } else {
            return instanceHolder.computeIfAbsent("default", (k) -> new BuzCacheImpl());
//            List<Class<?>> classList = new ArrayList<>();
//            String paths = System.getProperty("java.class.path");
//            for (String path : paths.split(";")) {
//                classList.addAll(scanPackageAndFindMyFuckingClasses(path));
//            }
//
//            if (classList.size() != 0) {
//                return new BuzCacheImpl(aClass);
//            } else {
//                return new BuzCacheImpl();
//            }
        }
    }

//    private List<Class<?>> scanPackageAndFindMyFuckingClasses(String path) {
//        List<Class<?>> classList = new ArrayList<>();
//        String[] javas = path.split("main");
//        String pack = javas[javas.length - 1].replace("\\", ".").substring(1);
//        try (DirectoryStream<Path> pathStream = Files.newDirectoryStream(Paths.get(path))) {
//            for (Path path1 : pathStream) {
//                if (Files.isDirectory(path1)) {
//                    classList.addAll(scanPackageAndFindMyFuckingClasses(path1.toString()));
//                } else {
//                    String name = path1.getFileName().toString();
//                    if (name.contains(".class")) {
//                        Class<?> aClass = null;
//                        try {
//                            String nameC = pack.concat(".").concat(name.replace(".class", ""));
//                            aClass = Class.forName(nameC);
//                        } catch (ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        if (aClass != null && isContainNeededAnnotation(aClass)) {
//                            classList.add(aClass);
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return classList;
//    }
//
//    private static boolean isContainNeededAnnotation(Class<?> clazz) {
//        return clazz.isAnnotationPresent(Cacheable.class);
//    }

}
