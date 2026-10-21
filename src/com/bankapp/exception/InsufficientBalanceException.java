package com.bankapp.exception;

public class InsufficientBalanceException extends BankException {
    public InsufficientBalanceException(String message) { super(message); }
}
