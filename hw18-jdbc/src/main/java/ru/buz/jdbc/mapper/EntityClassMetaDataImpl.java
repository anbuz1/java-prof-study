package ru.buz.jdbc.mapper;

import ru.buz.annotations.IdField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> aClass;

    public EntityClassMetaDataImpl(Class<T> aClass) {
        this.aClass = aClass;
    }

    @Override
    public String getName() {
        return aClass.getSimpleName().toUpperCase(Locale.ROOT);
    }

    @Override
    public Constructor<T> getConstructor() {
        List<Field> allFields = getAllFields();
        Class<?>[] classList = new Class[allFields.size()];
        for (int i = 0; i < allFields.size(); i++) {
            classList[i] = allFields.get(i).getType();
        }
        try {
            return aClass.getConstructor(classList);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Field getIdField() {
        List<Field> allFields = getAllFields();
        for (Field field : allFields) {
            if (field.isAnnotationPresent(IdField.class)) {
                return field;
            }
        }
        return null;
    }

    @Override
    public List<Field> getAllFields() {
        return Arrays.asList(aClass.getDeclaredFields());
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        List<Field> resultList = new ArrayList<>();
        for (Field declaredField : aClass.getDeclaredFields()) {
            if (!declaredField.isAnnotationPresent(IdField.class)) {
                resultList.add(declaredField);
            }
        }
        return resultList;
    }
}
