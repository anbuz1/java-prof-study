package ru.buz.exceptions;

public class NoAccountFoundException extends ATMExceptions  {
    public NoAccountFoundException(String message) {
        super(message);
    }
}
