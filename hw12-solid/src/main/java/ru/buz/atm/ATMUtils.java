package ru.buz.atm;

import ru.buz.exceptions.NoUserFoundException;
@FunctionalInterface
public interface ATMUtils {
    ATMImpl getATMInstanceWithAuthorizedUser(String name, String pass) throws NoUserFoundException;
}
