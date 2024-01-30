package com.example.projektpo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BazaDanych {
    private String url = "jdbc:mysql://localhost:3306/Baza";
    private String user = "root";
    private String password = "";

    public Connection connect() {
        Connection conn = null;
        try {
            // Włącz sterownik JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Nawiązanie połączenia z bazą danych
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Połączono z bazą danych.");
        } catch (ClassNotFoundException e) {
            System.out.println("Nie można znaleźć sterownika JDBC MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Błąd połączenia z bazą danych.");
            e.printStackTrace();
        }

        return conn;
    }

    // Metody do zarządzania danymi, np. dodawanie klienta

    // Metoda do zamykania połączenia (wywoływana w odpowiednim miejscu w kodzie)
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Zamknięto połączenie z bazą danych.");
            } catch (SQLException e) {
                System.out.println("Błąd podczas zamykania połączenia z bazą danych.");
                e.printStackTrace();
            }
        }
    }
}
