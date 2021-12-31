package ru.buz.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final EntityClassMetaData<?> aClass;
    private final String SELECT = "select %s from %s ";
    private final String WHERE = " where id = ? ";
    private final String INSERT = "insert into %s ";
    private final String UPDATE = "update %s set ";

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> aClass) {
        this.aClass = aClass;
    }

    @Override
    public String getSelectAllSql() {

        return String.format(SELECT, "*", aClass.getName().toUpperCase(Locale.ROOT));
    }

    @Override
    public String getSelectByIdSql() {

        return String.format(SELECT, "*", aClass.getName().toUpperCase(Locale.ROOT) + WHERE);
    }

    @Override
    public String getInsertSql() {
        List<Field> fieldsWithoutId = aClass.getFieldsWithoutId();
        StringBuilder builderField = new StringBuilder("(");
        StringBuilder builderVal = new StringBuilder("(");
        int count = 0;
        for (Field field : fieldsWithoutId) {
            count++;
            builderField.append(field.getName());
            builderField.append(count == fieldsWithoutId.size() ? ")" : ",");
            builderVal.append("?");
            builderVal.append(count == fieldsWithoutId.size() ? ")" : ",");
        }
        return String.format(INSERT, aClass.getName().toUpperCase(Locale.ROOT))
                + builderField
                + " values "
                + builderVal;
    }

    @Override
    public String getUpdateSql() {
        List<Field> fieldsWithoutId = aClass.getFieldsWithoutId();
        StringBuilder builderField = new StringBuilder();
        int count = 0;
        for (Field field : fieldsWithoutId) {
            count++;
            builderField.append(field.getName()).append(" = ?");
            builderField.append(count == fieldsWithoutId.size() ? " " : ",");
        }
        return String.format(UPDATE, aClass.getName().toUpperCase(Locale.ROOT))
                + builderField
                + WHERE;
    }
}
