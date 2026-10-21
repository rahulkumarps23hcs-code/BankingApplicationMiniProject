package com.bankapp.model;

import java.sql.Timestamp;

public class Transaction {
	
	private final int transactionId;
	private final long accountNumber;
	private final String type;
	private final double amount;
	private final String description;
	private final Timestamp date;
	
	public Transaction(int transactionId, long accountNumber, String type, double amount, String description, Timestamp date  ) {
	 super();
	this.transactionId = transactionId;
	this.accountNumber = accountNumber;
	this.type = type;
	this.amount = amount;
	this.description = description;
	this.date = date;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public long getAccountNumber() {
		return accountNumber;
	}

	public String getType() {
		return type;
	}

	public double getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public Timestamp getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "\n" + " Transaction" + "\n" + " transactionId = " + transactionId +  "\n" + " accountNumber = " + accountNumber + "\n" + " type = " + type
				+ "\n" + " amount = " + amount + "\n" + " description = " + description + "\n" + " date = " + date ;
	}
	
	
	
}
