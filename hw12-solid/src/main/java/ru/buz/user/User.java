package ru.buz.user;

import java.util.List;
import java.util.Optional;

public class User {
    private final String name;
    private final List<Account> accounts;

    public User(String name, List<Account> accounts) {
        this.name = name;
        this.accounts = accounts;
    }

    public String getName() {
        return name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Optional<Account> getAccount(long id) {
        for (Account account : accounts) {
            if (account.getAccID() == id)
                return Optional.of(account);
        }
        return Optional.empty();
    }
}
