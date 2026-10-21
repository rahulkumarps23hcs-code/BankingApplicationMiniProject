package com.bankapp.service;

import com.bankapp.dao.AccountDAO;
import com.bankapp.dao.CustomerDAO;
import com.bankapp.dao.TransactionDAO;
import com.bankapp.exception.AccountNotFoundException;
import com.bankapp.exception.AuthenticationException;
import com.bankapp.exception.BankException;
import com.bankapp.exception.InvalidInputException;
import com.bankapp.model.Account;
import com.bankapp.model.Customer;
import com.bankapp.model.Transaction;
import com.bankapp.ops.BankOperations;
import com.bankapp.util.ValidationUtil;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BankingServiceImpl implements BankingService {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private Customer currentUser;

    // ---------------- session ----------------
    @Override
    public void ensureDefaultAdmin() {
        try {
            if (!customerDAO.adminExists()) {
                Customer admin = new Customer(0, "Bank Admin", "admin@bank.com", "9000000000", "Head Office", "ADMIN");
                customerDAO.register(admin, "Admin@123", "ADMIN");
                System.out.println("[Setup] Default admin created -> admin@bank.com / Admin@123");
            }
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public Customer register(String name, String email, String mobile, String address, String password) {
        ValidationUtil.requireName(name);
        ValidationUtil.requireEmail(email);
        ValidationUtil.requireMobile(mobile);
        ValidationUtil.requirePassword(password);
        try {
            Customer c = new Customer(0, name.trim(), email.trim().toLowerCase(), mobile, address, "CUSTOMER");
            c.setCustomerId(customerDAO.register(c, password, "CUSTOMER"));
            return c;
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public Customer login(String email, String password, boolean admin) {
        try {
            currentUser = customerDAO.authenticate(email.trim().toLowerCase(), password, admin ? "ADMIN" : "CUSTOMER");
            return currentUser;
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override public void logout() { currentUser = null; }
    @Override public Customer getCurrentUser() { return currentUser; }

    // ---------------- customer ----------------
    @Override
    public long createAccount(String accountType, double openingBalance) {
        requireLogin();
        if (openingBalance < 0) throw new InvalidInputException("Opening balance cannot be negative.");
        try {
            return accountDAO.create(currentUser.getCustomerId(), accountType, openingBalance);
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public List<Account> getMyAccounts() {
        requireLogin();
        try {
            return accountDAO.findByCustomer(currentUser.getCustomerId());
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public Account getAccountDetails(long accountNumber) {
        return ownedAccount(accountNumber);
    }

    @Override
    public void deposit(long accountNumber, double amount) {
        checkAmount(amount);
        ownedAccount(accountNumber);
        try {
            accountDAO.deposit(accountNumber, amount);
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public void withdraw(long accountNumber, double amount) {
        checkAmount(amount);
        ownedAccount(accountNumber);
        try {
            accountDAO.withdraw(accountNumber, amount);
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public double checkBalance(long accountNumber) {
        return ownedAccount(accountNumber).getBalance();
    }

    @Override
    public void transfer(long fromAccount, long toAccount, double amount) {
        checkAmount(amount);
        ownedAccount(fromAccount);
        try {
            accountDAO.transfer(fromAccount, toAccount, amount);
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public List<Transaction> getTransactions(long accountNumber) {
        ownedAccount(accountNumber);
        try {
            return transactionDAO.findByAccount(accountNumber);
        } catch (SQLException e) {
            throw db(e);
        }
    }

    // ---------------- admin ----------------
    @Override
    public List<Customer> getAllCustomers() {
        requireAdmin();
        try {
            return customerDAO.findAll();
        } catch (SQLException e) {
            throw db(e);
        }
    }

    /** Iterator is used to safely remove non-matching customers while traversing. */
    @Override
    public List<Customer> searchCustomers(String keyword) {
        List<Customer> customers = getAllCustomers();
        String k = keyword.toLowerCase();
        Iterator<Customer> it = customers.iterator();
        while (it.hasNext()) {
            Customer c = it.next();
            boolean match = c.getName().toLowerCase().contains(k)
                    || c.getEmail().toLowerCase().contains(k)
                    || c.getMobile().contains(k)
                    || String.valueOf(c.getCustomerId()).equals(k);
            if (!match) it.remove();
        }
        return customers;
    }

    /** Stream + groupingBy -> Map of customerId to that customer's accounts. */
    @Override
    public Map<Integer, List<Account>> getAllAccountsByCustomer() {
        requireAdmin();
        try {
            return accountDAO.findAll().stream().collect(Collectors.groupingBy(Account::getCustomerId));
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public void setAccountStatus(long accountNumber, boolean active) {
        requireAdmin();
        try {
            if (!accountDAO.updateStatus(accountNumber, active ? "ACTIVE" : "INACTIVE"))
                throw new AccountNotFoundException(accountNumber);
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() {
        requireAdmin();
        try {
            return transactionDAO.findAll();
        } catch (SQLException e) {
            throw db(e);
        }
    }

    // ---------------- helpers ----------------
    /** A Set of the user's account numbers is used for the ownership check. */
    private Account ownedAccount(long accountNumber) {
        requireLogin();
        try {
            Set<Long> owned = accountDAO.findByCustomer(currentUser.getCustomerId()).stream()
                    .map(Account::getAccountNumber).collect(Collectors.toCollection(HashSet::new));
            if (!owned.contains(accountNumber))
                throw new BankException("Account " + accountNumber + " does not belong to you.");
            return accountDAO.findByNumber(accountNumber);
        } catch (SQLException e) {
            throw db(e);
        }
    }

    private void checkAmount(double amount) {
        if (!BankOperations.isValidAmount(amount))
            throw new InvalidInputException("Amount must be greater than zero.");
    }

    private void requireLogin() {
        if (currentUser == null) throw new AuthenticationException("Please login first.");
    }

    private void requireAdmin() {
        requireLogin();
        if (!"ADMIN".equals(currentUser.getRole())) throw new AuthenticationException("Admin access required.");
    }

    private BankException db(SQLException e) {
        return new BankException("Database error: " + e.getMessage(), e);
    }
}
