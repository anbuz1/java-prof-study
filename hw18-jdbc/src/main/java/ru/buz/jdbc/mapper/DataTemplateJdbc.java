package ru.buz.jdbc.mapper;

import ru.buz.annotations.IdField;
import ru.buz.core.repository.DataTemplate;
import ru.buz.core.repository.executor.DbExecutor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(),
                Collections.singletonList(id), rsHandler());
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), rsHandler(new ArrayList<>()));
    }

    @Override
    public long insert(Connection connection, T client) {
        List<Object> resultList = new ArrayList<>();
        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        for (Field field : fieldsWithoutId) {
            try {
                field.setAccessible(true);
                resultList.add(field.get(client));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), resultList);
    }

    @Override
    public void update(Connection connection, T client) {
        List<Object> resultList = new ArrayList<>();
        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        try {
            for (Field field : fieldsWithoutId) {
                field.setAccessible(true);
                resultList.add(field.get(client));
            }
            Field fieldId = entityClassMetaData.getIdField();
            fieldId.setAccessible(true);
            resultList.add(fieldId.get(client));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), resultList);
    }

    private Function<ResultSet, T> rsHandler(){
        return (rs) ->{
            List<Field> allFields = entityClassMetaData.getAllFields();
            Object[] arrObj = new Object[allFields.size()];
            try {
                while (rs.next()) {
                    for (int i = 0; i < allFields.size(); i++) {
                        String fieldName = allFields.get(i).getName();
                        if (allFields.get(i).isAnnotationPresent(IdField.class)) {
                            try {
                                arrObj[i] = rs.getObject(fieldName);
                            }catch (SQLException ex){
                                arrObj[i] = rs.getObject("id");
                            }
                        } else {
                            arrObj[i] = rs.getObject(fieldName);
                        }
                    }
                }
                return entityClassMetaData.getConstructor().newInstance(arrObj);
            } catch (SQLException | InstantiationException
                    | IllegalAccessException | InvocationTargetException throwables) {
                throwables.printStackTrace();
                return null;
            }
        };
    }
    private Function<ResultSet, List<T>> rsHandler(List<T> resultList){
        return (rs) ->{
            List<Field> allFields = entityClassMetaData.getAllFields();
            Object[] arrObj = new Object[allFields.size()];
            try {
                while (rs.next()) {
                    for (int i = 0; i < allFields.size(); i++) {
                        String fieldName = allFields.get(i).getName();
                        if (allFields.get(i).isAnnotationPresent(IdField.class)) {
                            try {
                                arrObj[i] = rs.getObject(fieldName);
                            }catch (SQLException ex){
                                arrObj[i] = rs.getObject("id");
                            }
                        } else {
                            arrObj[i] = rs.getObject(fieldName);
                        }
                    }
                    resultList.add(entityClassMetaData.getConstructor().newInstance(arrObj));
                }
                return resultList;
            } catch (SQLException | InstantiationException
                    | IllegalAccessException | InvocationTargetException throwables) {
                throwables.printStackTrace();
                return null;
            }
        };
    }

}
