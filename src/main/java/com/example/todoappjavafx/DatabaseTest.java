package com.example.todoappjavafx;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        DatabaseConnector connector = new DatabaseConnector();

        System.out.println("Próba połączenia z bazą...");

        try (Connection conn = connector.getConnection()) {
            if (conn != null) {
                System.out.println("SUKCES! Połączono z bazą danych w chmurze.");
            }
        } catch (SQLException e) {
            System.err.println("BŁĄD: Nie udało się połączyć.");
            e.printStackTrace();
        }
    }
}