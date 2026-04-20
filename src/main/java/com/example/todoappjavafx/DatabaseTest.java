package com.example.todoappjavafx;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        DatabaseConnector connector = new DatabaseConnector();

        System.out.println("try to connect");

        try (Connection conn = connector.getConnection()) {
            if (conn != null) {
                System.out.println("stattus ok");
            }
        } catch (SQLException e) {
            System.err.println("status not ok");
            e.printStackTrace();
        }
    }
}