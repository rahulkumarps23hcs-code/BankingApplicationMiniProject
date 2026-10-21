package com.bankapp.service;

import com.bankapp.model.Account;
import com.bankapp.model.Customer;
import com.bankapp.model.Transaction;
import com.bankapp.ops.BankOperations;
import java.util.List;
import java.util.Map;

public interface BankingService extends BankOperations {

    // session
    void ensureDefaultAdmin();
    Customer register(String name, String email, String mobile, String address, String password);
    Customer login(String email, String password, boolean admin);
    void logout();
    Customer getCurrentUser();

    // customer
    long createAccount(String accountType, double openingBalance);
    List<Account> getMyAccounts();
    Account getAccountDetails(long accountNumber);
    List<Transaction> getTransactions(long accountNumber);

    // admin
    List<Customer> getAllCustomers();
    List<Customer> searchCustomers(String keyword);
    Map<Integer, List<Account>> getAllAccountsByCustomer();
    void setAccountStatus(long accountNumber, boolean active);
    List<Transaction> getAllTransactions();
}
