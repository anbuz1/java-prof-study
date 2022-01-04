package ru.buz.atm;

import ru.buz.currency.ATMCurrency;
import ru.buz.exceptions.ATMExceptions;
import ru.buz.currency.CurrencyValue;

import java.util.List;
import java.util.Set;

public interface MoneyVault {
    long loadMoney(ATMCurrency[] currencies);
    List<ATMCurrency> giveMoney(int amount, java.util.Currency currency) throws ATMExceptions;
    Set<CurrencyValue> getAvailableCurrencyValues(java.util.Currency currency);
    long getAvailableAmount();
}
