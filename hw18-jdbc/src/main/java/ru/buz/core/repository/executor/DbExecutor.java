package ru.buz.core.repository.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface DbExecutor {

    long executeStatement(Connection connection, String sql, List<Object> params);

    <T> Optional<T> executeSelect(Connection connection, String sql, List<Object> params, Function<ResultSet, T> rsHandler) ;

    <T> List<T> executeSelect(Connection connection, String selectAllSql, Function<ResultSet,List<T>> rsHandler);
}
