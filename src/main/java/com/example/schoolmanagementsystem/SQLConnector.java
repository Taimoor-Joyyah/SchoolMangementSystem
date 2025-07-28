package com.example.schoolmanagementsystem;

import java.sql.*;

public class SQLConnector {
    private static final Statement statement;

    static {
        try {
            statement = DriverManager.getConnection("jdbc:mysql://localhost:3306/management", "root", "root").createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet executeQuery(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String executeQueryFunction(String sql) {
        ResultSet set = executeQuery(sql);
        try {
            set.next();
            return set.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean execute(String sql) {
        try {
            return statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
