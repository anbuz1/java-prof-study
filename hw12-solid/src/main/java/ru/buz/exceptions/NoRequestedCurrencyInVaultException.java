package ru.buz.exceptions;

public class NoRequestedCurrencyInVaultException extends ATMExceptions {
    public NoRequestedCurrencyInVaultException(String e) {
        super(e);
    }
}
