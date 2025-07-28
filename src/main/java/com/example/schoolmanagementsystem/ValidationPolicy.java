package com.example.schoolmanagementsystem;

public class ValidationPolicy {
    public static boolean isValidPassword(String password) {
        boolean digit = false;
        boolean alphabet = false;
        boolean special = false;

        for (char ch : password.toCharArray()) {
            if (Character.isDigit(ch))
                digit = true;
            else if (Character.isAlphabetic(ch))
                alphabet = true;
            else if (ch >= 32 && ch < 48)
                special = true;
            if (digit && alphabet && special)
                return true;
        }
        return false;
    }

    public static boolean isAllDigit(String number) {
        for (char ch : number.toCharArray()) {
            if (!Character.isDigit(ch))
                return false;
        }
        return true;
    }
}
