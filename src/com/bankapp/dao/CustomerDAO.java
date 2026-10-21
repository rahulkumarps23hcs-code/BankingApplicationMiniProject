package com.bankapp.dao;

import com.bankapp.exception.AuthenticationException;
import com.bankapp.exception.InvalidInputException;
import com.bankapp.model.Customer;
import com.bankapp.util.DBConnection;
import com.bankapp.util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public int register(Customer c, String password, String role) throws SQLException {
        String sql = "INSERT INTO customer(name,email,mobile,address,password_hash,role) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getMobile());
            ps.setString(4, c.getAddress());
            ps.setString(5, PasswordUtil.hash(password));
            ps.setString(6, role);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new InvalidInputException("Email is already registered.");
        }
    }

    /** Verifies credentials; the hash never leaves this class. */
    public Customer authenticate(String email, String password, String role) throws SQLException {
        String sql = "SELECT * FROM customer WHERE email = ? AND role = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify(password, rs.getString("password_hash")))
                    return map(rs);
            }
        }
        throw new AuthenticationException("Invalid email or password.");
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer WHERE role = 'CUSTOMER' ORDER BY customer_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean adminExists() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM customer WHERE role='ADMIN'");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    private Customer map(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt("customer_id"), rs.getString("name"), rs.getString("email"),
                rs.getString("mobile"), rs.getString("address"), rs.getString("role"));
    }
}
