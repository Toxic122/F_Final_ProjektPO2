package com.example.projektpo; // Deklaracja pakietu

import java.sql.Connection; // Import klasy Connection z pakietu java.sql do zarządzania połączeniem z bazą danych
import java.sql.DriverManager; // Import klasy DriverManager do zarządzania sterownikami bazy danych
import java.sql.SQLException; // Import klasy SQLException do obsługi wyjątków związanych z bazą danych

public class BazaDanych { // Deklaracja klasy BazaDanych
    private String url = "jdbc:mysql://localhost:3306/Baza"; // URL połączenia z bazą danych
    private String user = "root"; // Nazwa użytkownika bazy danych
    private String password = ""; // Hasło do bazy danych

    public Connection connect() { // Metoda do nawiązywania połączenia z bazą danych
        Connection conn = null; // Inicjalizacja zmiennej conn przechowującej połączenie z bazą danych
        try {
            // Włącz sterownik JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Nawiązanie połączenia z bazą danych
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Połączono z bazą danych."); // Informacja o pomyślnym połączeniu
        } catch (ClassNotFoundException e) { // Obsługa wyjątku braku sterownika JDBC
            System.out.println("Nie można znaleźć sterownika JDBC MySQL.");
            e.printStackTrace(); // Wyświetlenie śladu stosu wyjątku
        } catch (SQLException e) { // Obsługa wyjątku SQL
            System.out.println("Błąd połączenia z bazą danych.");
            e.printStackTrace(); // Wyświetlenie śladu stosu wyjątku
        }

        return conn; // Zwrócenie obiektu połączenia z bazą danych
    }

    // Metody do zarządzania danymi, np. dodawanie klienta
    // (tutaj miejsce na dodatkowe metody do operacji na bazie danych)

    // Metoda do zamykania połączenia (wywoływana w odpowiednim miejscu w kodzie)
    public void closeConnection(Connection conn) { // Metoda do zamykania połączenia z bazą danych
        if (conn != null) { // Sprawdzenie, czy połączenie istnieje
            try {
                conn.close(); // Zamknięcie połączenia
                System.out.println("Zamknięto połączenie z bazą danych."); // Informacja o zamknięciu połączenia
            } catch (SQLException e) { // Obsługa wyjątku SQL
                System.out.println("Błąd podczas zamykania połączenia z bazą danych.");
                e.printStackTrace(); // Wyświetlenie śladu stosu wyjątku
            }
        }
    }
}
