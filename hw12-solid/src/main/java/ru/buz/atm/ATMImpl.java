package ru.buz.atm;

import ru.buz.currency.Currency;
import ru.buz.exceptions.*;
import ru.buz.currency.CurrencyValue;
import ru.buz.user.Account;
import ru.buz.user.AccountDetails;
import ru.buz.user.User;
import ru.buz.utils.CrudUtility;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/*
The design idea is that an instance of an ATM class for use can only be obtained for a specific
authorized user. For this, the possibility of inheriting and creating an instance directly is excluded. First you need
initialize the class by passing it the main dependencies using the getATMInstanceWithAuthorizedUser(CrudUtility, MoneyVault)
 method - this method returns a link to a function with the help of which, having passed the user parameters to it,
 we get a link to an ATM instance with bound authorized user. Using the link to the function, we can authorize
 new users by receiving the same ATM copy.
 */

//This implementation only for single thread usage
public final class ATMImpl implements ATM {
    private final CrudUtility crudUtility;
    private final MoneyVault moneyVault;
    private static ATMImpl atm;
    private User user;
    private final ATMUtils atmUtils = new ATMUtils() {
        @Override
        public ATMImpl getATMInstanceWithAuthorizedUser(String name, String pass) throws NoUserFoundException {
            user = crudUtility.getUserFromDB(name, pass);
            if (user != null) {
                return atm;
            } else throw new NoUserFoundException("name or pass are not correct");

        }
    };

    private ATMImpl(CrudUtility crudUtility, MoneyVault moneyVault) {
        this.crudUtility = crudUtility;
        this.moneyVault = moneyVault;
    }

    public static ATMUtils initializeATM(CrudUtility crudUtility, MoneyVault moneyVault) {
        if (atm == null) {
            atm = new ATMImpl(crudUtility, moneyVault);
        }
        return atm.atmUtils;
    }

    @Override
    public List<AccountDetails> getAccounts() {
        return user.getAccounts().stream().map(Account::getAccountDetails).collect(Collectors.toList());
    }

    @Override
    public long getBalance(long accountID) throws NoAccountFoundException {
        Account account = user.getAccount(accountID)
                .orElseThrow(() -> new NoAccountFoundException("Didn't find account with id: " + accountID));
        return account.getBalance();
    }

    @Override
    public Set<CurrencyValue> getAvailableValuesForCurrency(java.util.Currency currency) {
        return moneyVault.getAvailableCurrencyValues(currency);
    }

    @Override
    public List<Currency> giveOutMoney(int amount, long accountID) throws ATMExceptions {
        Account account = user.getAccount(accountID)
                .orElseThrow(() -> new NoAccountFoundException("Didn't find account with id: " + accountID));
        if (account.getBalance() >= amount) {
            List<Currency> currencyImplList = moneyVault.giveMoney(amount, account.getCurrency());
            account.decrease(amount);
            crudUtility.updateAccount(user,account);
            return currencyImplList;
        } else throw new NotEnoughMoneyException("In your account not enough money");

    }


    @Override
    public boolean loadMoney(long accountID, Currency[] currencies) throws NoAccountFoundException {
        Account account = user.getAccount(accountID)
                .orElseThrow(() -> new NoAccountFoundException("Didn't find account with id: " + accountID));
        long amount = moneyVault.loadMoney(currencies);
        account.increase(amount);
        crudUtility.updateAccount(user,account);
        return true;
    }

}
