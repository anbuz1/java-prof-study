package ru.buz.atm;

import ru.buz.exceptions.*;
import ru.buz.currency.CurrencyValue;
import ru.buz.user.AccountDetails;

import java.util.Currency;
import java.util.List;
import java.util.Set;

public interface ATM {
//    ATM authorize(String name, String pass) throws NoUserFoundException;
    long getBalance(long accountID) throws NoAccountFoundException;
    List<AccountDetails> getAccounts();
    Set<CurrencyValue> getAvailableValuesForCurrency(Currency currency);
    List<ru.buz.currency.Currency> giveOutMoney(int amount, long accountID) throws ATMExceptions;
    boolean loadMoney(long accountID, ru.buz.currency.Currency[] currencies) throws NoAccountFoundException;
}
