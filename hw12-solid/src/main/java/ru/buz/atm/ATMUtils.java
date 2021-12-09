package ru.buz.atm;

@FunctionalInterface
public interface ATMUtils {
    ATMImpl getATMInstanceWithAuthorizedUser(String name, String pass) throws NoUserFoundException;
}
