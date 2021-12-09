package ru.buz.currency;

import java.util.Currency;
import java.util.Objects;

public final class ATMCurrency {
    private final CurrencyValue value;
    private final Currency currency;

    public ATMCurrency(CurrencyValue value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }


    public CurrencyValue getCurrencyValue() {
        return value;
    }


    public String getCurrency() {
        return currency.getCurrencyCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ATMCurrency) obj;
        return Objects.equals(this.value, that.value) &&
                Objects.equals(this.currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, currency);
    }

    @Override
    public String toString() {
        return "ATMCurrency[" +
                "value=" + value + ", " +
                "currency=" + currency + ']';
    }


}
