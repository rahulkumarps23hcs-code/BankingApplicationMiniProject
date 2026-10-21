package com.bankapp.dao;

import com.bankapp.exception.AccountNotFoundException;

import com.bankapp.exception.BankException;
import com.bankapp.exception.InsufficientBalanceException;
import com.bankapp.model.Account;
import com.bankapp.model.CurrentAccount;
import com.bankapp.model.SavingAccount;
import com.bankapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    /** Creates an account (and opening-deposit transaction) and returns the new account number. */
    public long create(int customerId, String type, double openingBalance) throws SQLException {
        String sql = "INSERT INTO account(customer_id, account_type, balance) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setString(2, type);
                ps.setDouble(3, openingBalance);
                ps.executeUpdate();
                long number;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    number = rs.getLong(1);
                }
                if (openingBalance > 0)
                    TransactionDAO.record(con, number, "DEPOSIT", openingBalance, "Opening deposit");
                con.commit();
                return number;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public Account findByNumber(long accountNumber) throws SQLException {
        return queryOne("SELECT * FROM account WHERE account_number = ?", accountNumber);
    }

    public List<Account> findByCustomer(int customerId) throws SQLException {
        return queryList("SELECT * FROM account WHERE customer_id = ? ORDER BY account_number", customerId);
    }

    public List<Account> findAll() throws SQLException {
        return queryList("SELECT * FROM account ORDER BY account_number", null);
    }

    public void deposit(long accountNumber, double amount) throws SQLException {
        String sql = "UPDATE account SET balance = balance + ? WHERE account_number = ? AND status = 'ACTIVE'";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDouble(1, amount);
                ps.setLong(2, accountNumber);
                if (ps.executeUpdate() == 0) throw new AccountNotFoundException(accountNumber);
                TransactionDAO.record(con, accountNumber, "DEPOSIT", amount, "Cash deposit");
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void withdraw(long accountNumber, double amount) throws SQLException {
        String sql = "UPDATE account SET balance = balance - ? "
                   + "WHERE account_number = ? AND status = 'ACTIVE' AND balance >= ?";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDouble(1, amount);
                ps.setLong(2, accountNumber);
                ps.setDouble(3, amount);
                if (ps.executeUpdate() == 0) {
                    Account acc = findByNumber(accountNumber);
                    if (acc == null || !"ACTIVE".equals(acc.getStatus()))
                        throw new AccountNotFoundException(accountNumber);
                    throw new InsufficientBalanceException("Insufficient balance. Available: "
                            + String.format("%.2f", acc.getBalance()));
                }
                TransactionDAO.record(con, accountNumber, "WITHDRAW", amount, "Cash withdrawal");
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** Money transfer via the transfer_money stored procedure. */
    public void transfer(long from, long to, double amount) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             CallableStatement cs = con.prepareCall("{call transfer_money(?, ?, ?)}")) {
            cs.setLong(1, from);
            cs.setLong(2, to);
            cs.setDouble(3, amount);
            cs.execute();
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                String msg = e.getMessage();
                if (msg != null && msg.toLowerCase().contains("insufficient"))
                    throw new InsufficientBalanceException(msg);
                throw new BankException(msg);
            }
            throw e;
        }
    }

    public boolean updateStatus(long accountNumber, String status) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE account SET status = ? WHERE account_number = ?")) {
            ps.setString(1, status);
            ps.setLong(2, accountNumber);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- helpers ----------
    private Account queryOne(String sql, long param) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private List<Account> queryList(String sql, Integer param) throws SQLException {
        List<Account> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param != null) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Account map(ResultSet rs) throws SQLException {
        long no = rs.getLong("account_number");
        int cid = rs.getInt("customer_id");
        double bal = rs.getDouble("balance");
        String status = rs.getString("status");
        return "SAVINGS".equals(rs.getString("account_type"))
                ? new SavingAccount(no, cid, bal, status)
                : new CurrentAccount(no, cid, bal, status);
    }
}
