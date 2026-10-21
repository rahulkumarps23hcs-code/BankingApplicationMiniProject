package com.bankapp.model;

public class Customer {

    private int customerId;
    private String name;
    private String email;
    private String mobile;
    private String address;
    private String role = "CUSTOMER";

    public Customer() {
    }

    public Customer(int customerId, String name, String email,
                    String mobile, String address, String role) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.role = role;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // toString() method
    @Override
    public String toString() {
        return "Customer {" +
                "Customer ID = " + customerId +
                ", Name = '" + name + '\'' +
                ", Email = '" + email + '\'' +
                ", Mobile = '" + mobile + '\'' +
                ", Address = '" + address + '\'' +
                ", Role = '" + role + '\'' +
                '}';
    }
}