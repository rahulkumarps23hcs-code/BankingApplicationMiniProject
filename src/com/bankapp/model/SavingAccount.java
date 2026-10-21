package com.bankapp.model;

public class SavingAccount extends Account {
	
	public static final double INTEREST_RATE = 0.04;

	public SavingAccount(long accountNumber, int customerId, double balance, String status) {
		super(accountNumber, customerId, balance, status);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getAccountType() {
		// TODO Auto-generated method stub
		return "SAVINGS";
	}

	@Override
	public double calculateInterest() {
		// TODO Auto-generated method stub
		return getBalance() * INTEREST_RATE;
	}
	
	 @Override
	    public String toString() {
	        return "\n" + " Account Number  : " + getAccountNumber() + "\n" +
	               "Customer ID     : " + getCustomerId() + "\n" +
	               "Account Type    : " + getAccountType() + "\n" +
	               "Balance         : " + getBalance() + "\n" +
	               "Status          : " + getStatus() + "\n" +
	               "Yearly Interest : " + calculateInterest();
	    }

}
