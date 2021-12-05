package ru.buz.currency;

import ru.buz.exceptions.CurrencyValueException;
import static ru.buz.utils.CurrencyUtil.checkCurrencyValue;

public final class CurrencyImpl implements Currency {
    private final CurrencyValue value;
    private final java.util.Currency currency;

    private CurrencyImpl(CurrencyValue value, java.util.Currency currency) {
        this.value = value;
        this.currency = currency;
    }

    @Override
    public CurrencyValue getCurrencyValue() {
        return value;
    }

    @Override
    public String getCurrency() {
        return currency.getCurrencyCode();
    }

    public static Currency getCurrencyInstance(CurrencyValue value, java.util.Currency currency) throws CurrencyValueException {
        if (checkCurrencyValue(value, currency)) {
            return new CurrencyImpl(value, currency);
        } else {
            throw new CurrencyValueException("Not correct value of currency");
        }
    }
}
