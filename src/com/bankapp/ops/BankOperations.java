package com.bankapp.ops;

/** Core banking operations (Java 8 interface with default + static methods). */
public interface BankOperations {

    void deposit(long accountNumber, double amount);

    void withdraw(long accountNumber, double amount);

    double checkBalance(long accountNumber);

    void transfer(long fromAccount, long toAccount, double amount);

    default void printWelcomeMessage() {
        System.out.println("Welcome to Yuva Sena Bank Rasayani!");
    }

    static boolean isValidAmount(double amount) {
        return amount > 0 && !Double.isNaN(amount) && !Double.isInfinite(amount);
    }
}
