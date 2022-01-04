package ru.buz.atm;

import ru.buz.currency.ATMCurrency;
import ru.buz.exceptions.*;
import ru.buz.currency.CurrencyValue;
import java.util.Currency;
import java.util.List;
import java.util.Set;

public final class ATMImpl implements ATM {
    private final MoneyVault moneyVault;

    ATMImpl(MoneyVault moneyVault) {
        this.moneyVault = moneyVault;
    }


    @Override
    public long getBalance() {
        return moneyVault.getAvailableAmount();
    }

    @Override
    public Set<CurrencyValue> getAvailableValuesForCurrency(java.util.Currency currency) {
        return moneyVault.getAvailableCurrencyValues(currency);
    }

    @Override
    public List<ATMCurrency> giveOutMoney(int amount) throws ATMExceptions {
        return moneyVault.giveMoney(amount, Currency.getInstance("RUB"));
    }


    @Override
    public void loadMoney(ATMCurrency[] currencies) {
        moneyVault.loadMoney(currencies);
    }
}
