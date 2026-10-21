package com.bankapp.main;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.bankapp.exception.BankException;
import com.bankapp.model.Account;
import com.bankapp.model.Customer;
import com.bankapp.service.BankingService;
import com.bankapp.service.BankingServiceImpl;

public class BankingApplication {

    private static final BankingService service = new BankingServiceImpl();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            service.ensureDefaultAdmin();
        } catch (BankException e) {
            System.out.println("Startup failed: " + e.getMessage());
            System.out.println("Check db.properties and sql/schema.sql.");
            return;
        }

        service.printWelcomeMessage();

        boolean running = true;

        while (running) {
            try {
                if (service.getCurrentUser() == null) {
                    running = mainMenu();

                } else if ("ADMIN".equals(
                        service.getCurrentUser().getRole())) {
                    adminMenu();

                } else {
                    customerMenu();
                }

            } catch (BankException e) {
                System.out.println("\n[Error] " + e.getMessage());
            }
        }

        System.out.println("Thank you for using Yuva Sena Bank Rasayani.");
    }

    private static boolean mainMenu() {

        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Customer Registration");
        System.out.println("2. Customer Login");
        System.out.println("3. Admin Login");
        System.out.println("4. Exit");

        System.out.print("Enter choice: ");
        int choice = Integer.parseInt(sc.nextLine());

        switch (choice) {
            case 1:
                register();
                break;

            case 2:
                login(false);
                break;

            case 3:
                login(true);
                break;

            case 4:
                return false;

            default:
                System.out.println("Invalid choice.");
        }

        return true;
    }

    private static void register() {

        System.out.println("\n===== CUSTOMER REGISTRATION =====");

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Mobile: ");
        String mobile = sc.nextLine();

        System.out.print("Address: ");
        String address = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        Customer customer = service.register(
                name,
                email,
                mobile,
                address,
                password
        );

        System.out.println(
                "Registration successful. Welcome " + customer.getName()
        );
    }

    private static void login(boolean admin) {

        System.out.println(
                admin ? "\n===== ADMIN LOGIN ====="
                      : "\n===== CUSTOMER LOGIN ====="
        );

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        Customer customer = service.login(email, password, admin);

        System.out.println("Welcome : " + customer.getName() + "!");
    }

    private static void customerMenu() {

        header("CUSTOMER MENU - " + service.getCurrentUser().getName());

        System.out.println("1. Create Bank Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. Check Balance");
        System.out.println("6. Transaction History");
        System.out.println("7. View Account Details");
        System.out.println("8. Logout");

        System.out.print("Enter choice: ");
        int choice = Integer.parseInt(sc.nextLine());

        switch (choice) {
            case 1:
                createAccount();
                break;

            case 2:
                deposit();
                break;

            case 3:
                withdraw();
                break;

            case 4:
                transfer();
                break;

            case 5:
                checkBalance();
                break;

            case 6:
                transactionHistory();
                break;

            case 7:
                accountDetails();
                break;

            case 8:
                service.logout();
                System.out.println("Logged out successfully.");
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void adminMenu() {

        header("ADMIN MENU");

        System.out.println("1. View All Customers");
        System.out.println("2. Search Customer");
        System.out.println("3. View All Accounts");
        System.out.println("4. Activate / Deactivate Account");
        System.out.println("5. View All Transactions");
        System.out.println("6. Logout");

        System.out.print("Enter choice: ");
        int choice = Integer.parseInt(sc.nextLine());

        switch (choice) {
            case 1:
                printCustomer(service.getAllCustomers());
                break;

            case 2:
                searchCustomer();
                break;

            case 3:
                printAllAccounts();
                break;

            case 4:
                changeAccountStatus();
                break;

            case 5:
                showTransactions(service.getAllTransactions());
                break;

            case 6:
                service.logout();
                System.out.println("Logged out successfully.");
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void createAccount() {

        System.out.print("Enter account type: ");
        String accountType = sc.nextLine();

        System.out.print("Enter opening balance: ");
        double openingBalance = Double.parseDouble(sc.nextLine());

        long accountNumber =
                service.createAccount(accountType, openingBalance);

        System.out.println(
                "Account created successfully. Account Number: "
                        + accountNumber
        );
    }

    private static void deposit() {

        System.out.print("Enter account number: ");
        long accountNumber = Long.parseLong(sc.nextLine());

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(sc.nextLine());

        service.deposit(accountNumber, amount);

        System.out.println("Amount deposited successfully.");
    }

    private static void withdraw() {

        System.out.print("Enter account number: ");
        long accountNumber = Long.parseLong(sc.nextLine());

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(sc.nextLine());

        service.withdraw(accountNumber, amount);

        System.out.println("Amount withdrawn successfully.");
    }

    private static void transfer() {

        System.out.print("Enter source account number: ");
        long fromAccount = Long.parseLong(sc.nextLine());

        System.out.print("Enter destination account number: ");
        long toAccount = Long.parseLong(sc.nextLine());

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(sc.nextLine());

        service.transfer(fromAccount, toAccount, amount);

        System.out.println("Amount transferred successfully.");
    }

    private static void checkBalance() {

        System.out.print("Enter account number: ");
        long accountNumber = Long.parseLong(sc.nextLine());

        Account account = service.getAccountDetails(accountNumber);

        System.out.println("Account Number: " + account.getAccountNumber());
        System.out.println("Balance: " + account.getBalance());
    }

    private static void transactionHistory() {

        System.out.print("Enter account number: ");
        long accountNumber = Long.parseLong(sc.nextLine());

        showTransactions(service.getTransactions(accountNumber));
    }

    private static void accountDetails() {

        System.out.print("Enter account number: ");
        long accountNumber = Long.parseLong(sc.nextLine());

        Account account = service.getAccountDetails(accountNumber);

        System.out.println(account);
    }

    // Search Customer
    private static void searchCustomer() {

        System.out.print("Enter name/email/mobile: ");
        String keyword = sc.nextLine();

        List<Customer> customers = service.searchCustomers(keyword);

        System.out.println("\n===== SEARCH CUSTOMER RESULTS =====");

        printCustomer(customers);
    }

    // View All Accounts
    private static void printAllAccounts() {

        Map<Integer, List<Account>> accounts =
                service.getAllAccountsByCustomer();

        System.out.println("\n===== ALL BANK ACCOUNTS =====");

        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (Map.Entry<Integer, List<Account>> entry
                : accounts.entrySet()) {

            System.out.println("\nCustomer ID: " + entry.getKey());

            for (Account account : entry.getValue()) {
                System.out.println(account.toString());
            }
        }
    }

    private static void changeAccountStatus() {

        System.out.print("Enter account number: ");
        long accountNumber = Long.parseLong(sc.nextLine());

        System.out.print(
                "Enter status (true = Active, false = Inactive): "
        );

        boolean active = Boolean.parseBoolean(sc.nextLine());

        service.setAccountStatus(accountNumber, active);

        System.out.println("Account status updated successfully.");
    }

    // View All Customers and Search Customer output
    private static void printCustomer(List<Customer> customers) {

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (Customer customer : customers) {
            System.out.println(customer.toString());
        }
    }

    private static void showTransactions(List<?> transactions) {

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Object transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private static void header(String title) {

        System.out.println();
        System.out.println("======================================");
        System.out.println(title);
        System.out.println("======================================");
    }
}
