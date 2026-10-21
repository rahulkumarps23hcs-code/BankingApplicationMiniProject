package com.bankapp.util;

import com.bankapp.exception.InvalidInputException;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern MOBILE = Pattern.compile("^[6-9]\\d{9}$");

    private ValidationUtil() { }

    public static void requireName(String name) {
        if (name == null || name.trim().length() < 2)
            throw new InvalidInputException("Name must be at least 2 characters.");
    }

    public static void requireEmail(String email) {
        if (email == null || !EMAIL.matcher(email).matches())
            throw new InvalidInputException("Invalid email address.");
    }

    public static void requireMobile(String mobile) {
        if (mobile == null || !MOBILE.matcher(mobile).matches())
            throw new InvalidInputException("Mobile must be a valid 10-digit number.");
    }

    public static void requirePassword(String password) {
        if (password == null || password.length() < 6
                || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*"))
            throw new InvalidInputException("Password must be 6+ characters with letters and digits.");
    }
}
