package ru.buz.user;

import java.util.Currency;
import java.util.Objects;

public class Account {
    private final long accID;
    private final Currency currency;
    private long balance;

    public Account(long accID, Currency currency, long balance) {
        this.accID = accID;
        this.currency = currency;
        this.balance = balance;
    }

    public long getAccID() {
        return accID;
    }

    public Currency getCurrency() {
        return currency;
    }

    public long getBalance() {
        return balance;
    }

    public AccountDetails getAccountDetails(){
        return new AccountDetails(String.valueOf(accID),currency.toString(),String.valueOf(balance));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accID == account.accID && balance == account.balance && currency.equals(account.currency);
    }

    public boolean equals(long id) {
        return id == accID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accID, currency, balance);
    }


    public void decrease(int amount) {
        balance -= amount;
    }

    public void increase(long amount) {
        balance += amount;
    }
}
