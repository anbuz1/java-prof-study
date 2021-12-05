package ru.buz.utils;

import ru.buz.user.Account;
import ru.buz.user.User;

public interface CrudUtility {
    User getUserFromDB(String name,String pass);
    boolean createNewUser(User user);
    boolean createNewAccount(User user, Account account);
    boolean updateAccount(User user, Account account);
    boolean deleteAccount(User user, Account account);
}
