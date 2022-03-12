package ru.buz.crm.session;

public interface TransactionManager {

    <T> T doInTransaction(TransactionAction<T> action);
}
