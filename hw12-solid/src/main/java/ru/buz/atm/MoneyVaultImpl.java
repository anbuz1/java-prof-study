package ru.buz.atm;

import ru.buz.currency.ATMCurrency;
import ru.buz.exceptions.ATMExceptions;
import ru.buz.exceptions.GiveOutMoneyException;
import ru.buz.exceptions.NoRequestedCurrencyInVaultException;
import ru.buz.exceptions.NotEnoughMoneyException;
import ru.buz.currency.CurrencyValue;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MoneyVaultImpl implements MoneyVault {

    private final Map<String, SortedSet<CurrencyValue>> mapCurrencyValues;
    private final Map<String, Map<CurrencyValue, List<ATMCurrency>>> mapCurrencies;
    private long availableAmount;

    public MoneyVaultImpl(List<ATMCurrency> currencies) {
        mapCurrencies = new HashMap<>();
        mapCurrencyValues = new HashMap<>();
        sortAndAddValues(currencies);
        availableAmount = loadMoney(currencies);

    }

    @Override
    public long loadMoney(ATMCurrency[] currencies) {
        return loadMoney(Arrays.asList(currencies));
    }

    public long loadMoney(List<ATMCurrency> currencies) {
        AtomicLong result = new AtomicLong();
        currencies.forEach(c -> {
            Map<CurrencyValue, List<ATMCurrency>> currencyListMap =
                    mapCurrencies.computeIfAbsent(c.getCurrency(), k -> new HashMap<>());
            List<ATMCurrency> currencyImplList =
                    currencyListMap.computeIfAbsent(c.getCurrencyValue(), k -> new ArrayList<>());
            result.addAndGet(c.getCurrencyValue().getValue());
            currencyImplList.add(c);
        });
        availableAmount += result.get();
        return result.get();
    }

    @Override
    public List<ATMCurrency> giveMoney(final int amount, java.util.Currency currency) throws ATMExceptions {
        Map<CurrencyValue, Integer> resultMap = new HashMap<>();
        int copyAmount = amount;
        String currCode = currency.getCurrencyCode();

        long availableAmount = getAvailableAmount(mapCurrencies.get(currCode));
        if (availableAmount < amount) throw new NotEnoughMoneyException("This ATM have not enough of money");

        for (CurrencyValue currencyValue : mapCurrencyValues.get(currCode)) {
            int tempValue = currencyValue.getValue();

            if (tempValue < copyAmount) {
                int res1 = copyAmount / tempValue;

                if (checkAvailableSumOfCurValues(currCode, currencyValue, res1)) {
                    resultMap.put(currencyValue, res1);
                    copyAmount = copyAmount - (currencyValue.getValue() * res1);
                } else {
                    int availableSum = getAvailableSumOfCurValues(currCode, currencyValue);
                    resultMap.put(currencyValue, availableSum);
                    copyAmount = copyAmount - (currencyValue.getValue() * availableSum);
                }


            } else if (tempValue == copyAmount) {
                resultMap.put(currencyValue, 1);
                copyAmount = 0;
                break;
            }
        }
        if (copyAmount != 0) {
            throw new GiveOutMoneyException("Can't give out requested amount");
        }
        return getMoneyFromVault(resultMap, currCode);
    }

    @Override
    public SortedSet<CurrencyValue> getAvailableCurrencyValues(java.util.Currency currency) {
        return mapCurrencyValues.get(currency.getCurrencyCode());
    }

    private void sortAndAddValues(List<ATMCurrency> currencies) {
        currencies.forEach(p -> {
            SortedSet<CurrencyValue> setOfCurrencyValues =
                    mapCurrencyValues.computeIfAbsent(p.getCurrency(),
                            k -> new TreeSet<>(Comparator.comparingInt(CurrencyValue::getValue).reversed()));
            setOfCurrencyValues.add(p.getCurrencyValue());
        });
    }

    private long getAvailableAmount(Map<CurrencyValue, List<ATMCurrency>> currencies) throws NoRequestedCurrencyInVaultException {
        if (currencies == null) {
            throw new NoRequestedCurrencyInVaultException("ATM has no requested currency");
        }
        AtomicLong result = new AtomicLong();
        currencies.forEach((key, value) -> value.forEach(cur -> result.addAndGet(cur.getCurrencyValue().getValue())));
        return result.get();
    }

    private boolean checkAvailableSumOfCurValues(String currCode, CurrencyValue currencyValue, int numb) {
        return mapCurrencies.get(currCode)
                .get(currencyValue).size() >= numb;
    }

    private int getAvailableSumOfCurValues(String currCode, CurrencyValue currencyValue) {
        return mapCurrencies.get(currCode)
                .get(currencyValue).size();
    }

    private List<ATMCurrency> getMoneyFromVault(Map<CurrencyValue, Integer> resultMap, String currCode) {
        long resultAmount = 0;
        List<ATMCurrency> resultListCurr = new ArrayList<>();
        for (CurrencyValue currencyValue : resultMap.keySet()) {
            List<ATMCurrency> tempListCurr = mapCurrencies.get(currCode).get(currencyValue);
            for (int i = 0; i < resultMap.get(currencyValue); i++) {
                ATMCurrency atmCurrency = tempListCurr.get(i);
                resultListCurr.add(atmCurrency);
                resultAmount+=atmCurrency.getCurrencyValue().getValue();
            }
            resultListCurr.forEach(tempListCurr::remove);
        }
        availableAmount -= resultAmount;
        return resultListCurr;
    }

    public long getAvailableAmount() {
        return availableAmount;
    }
}
