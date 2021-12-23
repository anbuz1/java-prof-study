package ru.buz.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.buz.currency.ATMCurrency;
import ru.buz.currency.CurrencyValue;
import ru.buz.exceptions.ATMExceptions;
import ru.buz.exceptions.GiveOutMoneyException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ATMImplTest {
    ATM atm;

    @BeforeEach
    void initialize()  {
        ATMCurrency[] currencies = {
                new ATMCurrency(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(200), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(200), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(200), java.util.Currency.getInstance("RUB")),
        };

        atm = new ATMImpl(new MoneyVaultImpl(Arrays.asList(currencies)));
    }


    @Test
    void getBalance() {
        assert atm != null;
        assertEquals(1500, atm.getBalance());
        assertNotEquals(1501, atm.getBalance());
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
        List<ATMCurrency> currencyImplList = atm.giveOutMoney(200);
        assertEquals(1, currencyImplList.size());
        assertEquals(200, currencyImplList.get(0).getCurrencyValue().getValue());
        assertEquals(1300, atm.getBalance());

        List<ATMCurrency> finalCurrencyImplList = atm.giveOutMoney(500);
        assertEquals(3, finalCurrencyImplList.size());
        assertTrue(() -> {
            int count_200 = 0;
            int count_100 = 0;
            for (ATMCurrency currencyImpl : finalCurrencyImplList) {
                if (currencyImpl.getCurrencyValue().getValue() == 200) {
                    count_200++;
                }
                if (currencyImpl.getCurrencyValue().getValue() == 100) {
                    count_100++;
                }
            }
            return count_200 == 2 & count_100 == 1;
        });
        assertEquals(800, atm.getBalance());
    }
    @Test
    void giveOutMoneyNegative(){
        assertThrows(GiveOutMoneyException.class,()->{atm.giveOutMoney(210);});
    }

    @Test
    void loadMoney() throws ATMExceptions {
        ATMCurrency[] currencies = {
                new ATMCurrency(new CurrencyValue(50), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(100), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(200), java.util.Currency.getInstance("RUB")),
                new ATMCurrency(new CurrencyValue(500), java.util.Currency.getInstance("RUB"))};
        assert atm != null;
        atm.loadMoney(currencies);
        assertEquals(2350, atm.getBalance());
    }
}