package com.bankapp.model;

public abstract class Account {
	
	private final long accountNumber;
	private final int customerId;
	private double balance;
	private String status;
	
	protected Account(long accountNumber, int customerId, double balance, String status) {
		this.accountNumber = accountNumber;
		this.customerId = customerId;
		this.balance = balance;
		this.status = status;
	}

	public long getAccountNumber() { return accountNumber;}	
	public int getCustomerId() { return customerId;}	
	public double getBalance() { return balance;}	
	public String getStatus() { return status;}	
	public void setBalance(double balance) {this.balance = balance;}
	public void setStatus(String status) {this.status = status;}
	
	public abstract String getAccountType();
	public abstract double calculateInterest();
	
	public void displayAccount() {
		System.out.println("Account Number  : "+ accountNumber);
		System.out.println("Account Type    : "+ getAccountType());
		System.out.println("Balance         : "+ String.format("%.2f",balance));
		System.out.println("Status          : "+ status);
		System.out.println("Yearly Interest : "+ String.format("%2f",calculateInterest()));
	}
	
	
}
