package ru.buz.atm;

import ru.buz.currency.ATMCurrency;
import ru.buz.exceptions.*;
import ru.buz.currency.CurrencyValue;
import ru.buz.user.AccountDetails;

import java.util.Currency;
import java.util.List;
import java.util.Set;

public interface ATM {

    long getBalance();
    Set<CurrencyValue> getAvailableValuesForCurrency(Currency currency);
    List<ATMCurrency> giveOutMoney(int amount) throws ATMExceptions;
    boolean loadMoney(ATMCurrency[] currencies) throws NoAccountFoundException;
}
