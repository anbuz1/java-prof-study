package ru.buz.exceptions;

public class NoEnoughMoneyException extends ATMExceptions{
    public NoEnoughMoneyException(String message) {
        super(message);
    }
}
