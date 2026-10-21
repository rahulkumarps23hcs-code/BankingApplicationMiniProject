package com.bankapp.model;

public class CurrentAccount extends Account {
    public CurrentAccount(long accountNumber, int customerId, double balance, String status) {
        super(accountNumber, customerId, balance, status);
    }

    @Override public String getAccountType() { return "CURRENT"; }
    @Override public double calculateInterest() { return 0; }
    
    @Override
    public String toString() {
        return "\n" + "Account Number  : " + getAccountNumber() + "\n" +
               "Customer ID     : " + getCustomerId() + "\n" +
               "Account Type    : " + getAccountType() + "\n" +
               "Balance         : " + getBalance() + "\n" +
               "Status          : " + getStatus() + "\n" +
               "Yearly Interest : " + calculateInterest();
    }
}
