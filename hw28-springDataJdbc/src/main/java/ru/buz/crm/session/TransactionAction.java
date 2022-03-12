package ru.buz.crm.session;

import java.util.function.Supplier;

public interface TransactionAction<T> extends Supplier<T> {
}
