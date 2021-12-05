package ru.buz.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.buz.currency.Currency;
import ru.buz.currency.CurrencyValue;
import ru.buz.exceptions.ATMExceptions;
import ru.buz.exceptions.CurrencyValueException;
import ru.buz.exceptions.GiveOutMoneyException;
import ru.buz.exceptions.NoUserFoundException;
import ru.buz.user.Account;
import ru.buz.user.AccountDetails;
import ru.buz.user.User;
import ru.buz.utils.CrudUtility;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.buz.atm.ATMImpl.initializeATM;
import static ru.buz.currency.CurrencyImpl.getCurrencyInstance;

class ATMImplTest {
    ATMUtils atmUtils;
    ATM atm;

    @BeforeEach
    void initialize() throws CurrencyValueException, NoUserFoundException {
        Currency[] currencies = {
                getCurrencyInstance(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(200), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(200), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(200), java.util.Currency.getInstance("RUB")),
        };
        atmUtils = initializeATM(new CrudUtility() {
                                     @Override
                                     public User getUserFromDB(String name, String pass) {
                                         return new User(name, Arrays.asList(
                                                 new Account(1234567890, java.util.Currency.getInstance("RUB"), 10_000),
                                                 new Account(9876543210L, java.util.Currency.getInstance("USD"), 10_000)));
                                     }

                                     @Override
                                     public boolean createNewUser(User user) {
                                         return true;
                                     }

                                     @Override
                                     public boolean createNewAccount(User user, Account account) {
                                         return true;
                                     }

                                     @Override
                                     public boolean updateAccount(User user, Account account) {
                                         return true;
                                     }

                                     @Override
                                     public boolean deleteAccount(User user, Account account) {
                                         return true;
                                     }
                                 },
                new MoneyVaultImpl(Arrays.asList(currencies)));
        atm = atmUtils.getATMInstanceWithAuthorizedUser("Andrei", "securepass");
    }

    @Test
    void getAccounts() {
        assert atm != null;
        List<AccountDetails> accounts = atm.getAccounts();
        assertTrue(() -> accounts != null);

    }

    @Test
    void getBalance() {
        assert atm != null;
        List<AccountDetails> accounts = atm.getAccounts();

        assertEquals(10000, Integer.valueOf(accounts.get(0).getBALANCE()));
        assertEquals(10000, Integer.valueOf(accounts.get(1).getBALANCE()));

    }

    @Test
    void getAvailableValuesForCurrency() {
        Set<CurrencyValue> testSet = Set.of(new CurrencyValue(200), new CurrencyValue(100), new CurrencyValue(50));
        assert atm != null;
        Set<CurrencyValue> availableValuesForCurrencyRub =
                atm.getAvailableValuesForCurrency(java.util.Currency.getInstance("RUB"));
        Set<CurrencyValue> availableValuesForCurrencyUsd =
                atm.getAvailableValuesForCurrency(java.util.Currency.getInstance("USD"));
        assertTrue(() -> {
            boolean flag = false;
            for (CurrencyValue currencyValue : testSet) {
                for (CurrencyValue currValue : availableValuesForCurrencyRub) {
                    if (currencyValue.getValue() == currValue.getValue()) {
                        flag = true;
                        break;
                    } else flag = false;
                }
                if (!flag) return false;
            }
            return true;
        });
        assertNull(availableValuesForCurrencyUsd);
    }

    @Test
    void giveOutMoney() throws ATMExceptions {
        assert atm != null;
        List<Currency> currencyImplList = atm.giveOutMoney(200, 1234567890);
        assertEquals(1, currencyImplList.size());
        assertEquals(200, currencyImplList.get(0).getCurrencyValue().getValue());
        assertEquals(9800, atm.getBalance(1234567890));

        List<Currency> finalCurrencyImplList = atm.giveOutMoney(500, 1234567890);
        assertEquals(3, finalCurrencyImplList.size());
        assertTrue(() -> {
            int count_200 = 0;
            int count_100 = 0;
            for (Currency currencyImpl : finalCurrencyImplList) {
                if (currencyImpl.getCurrencyValue().getValue() == 200) {
                    count_200++;
                }
                if (currencyImpl.getCurrencyValue().getValue() == 100) {
                    count_100++;
                }
            }
            return count_200 == 2 & count_100 == 1;
        });
        assertEquals(9300, atm.getBalance(1234567890));
    }
    @Test
    void giveOutMoneyNegative(){
        assertThrows(GiveOutMoneyException.class,()->{atm.giveOutMoney(210, 1234567890);});
    }

    @Test
    void loadMoney() throws ATMExceptions {
        Currency[] currencies = {
                getCurrencyInstance(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(200), java.util.Currency.getInstance("RUB")),
                getCurrencyInstance(new CurrencyValue(500), java.util.Currency.getInstance("RUB"))};
        assert atm != null;
        atm.loadMoney(1234567890, currencies);
        assertEquals(10850, atm.getBalance(1234567890));
    }
}