package com.bankapp.exception;

/** Base (unchecked) exception for all business errors. */
public class BankException extends RuntimeException {
    public BankException(String message) { super(message); }
    public BankException(String message, Throwable cause) { super(message, cause); }
}
