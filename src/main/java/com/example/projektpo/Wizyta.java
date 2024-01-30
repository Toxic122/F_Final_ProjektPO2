package com.example.projektpo; // Deklaracja pakietu, w którym znajduje się klasa

// Importowanie niezbędnych klas z biblioteki JavaFX i JDBC
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.scene.control.Label;

public class Wizyta { // Definicja klasy Wizyta
    private Stage stage; // Deklaracja zmiennej stage do przechowywania referencji do sceny
    private View previousView; // Zmienna przechowująca referencję do poprzedniego widoku
    private ComboBox<String> appointmentIdComboBox = new ComboBox<>(); // Deklaracja i inicjalizacja rozwijanego menu do wybierania ID wizyt

    // Konstruktor klasy Wizyta
    public Wizyta(Stage stage, View previousView) {
        this.stage = stage; // Przypisanie sceny do zmiennej klasy
        this.previousView = previousView; // Przypisanie poprzedniego widoku do zmiennej klasy
    }

    // Metoda do zarządzania wizytami
    public void showManageAppointments() {
        VBox layout = new VBox(10); // Tworzenie układu pionowego z odstępami

        ComboBox<String> clientComboBox = new ComboBox<>(); // Tworzenie rozwijanego menu do wybierania klienta
        loadClients(clientComboBox); // Ładowanie klientów do rozwijanego menu

        DatePicker datePicker = new DatePicker(LocalDate.now()); // Tworzenie selektora daty z domyślną wartością dzisiejszą
        TextField timeField = new TextField(); // Tworzenie pola tekstowego do wprowadzania czasu
        timeField.setPromptText("Wpisz Godzinę. Użyj formatu HH:mm"); // Ustawienie tekstu zachęty w polu tekstowym

        Button addAppointmentButton = new Button("Dodaj wizytę"); // Tworzenie przycisku do dodawania wizyt
        // Ustawienie akcji dla przycisku dodającego wizytę
        addAppointmentButton.setOnAction(e -> addAppointment(clientComboBox.getValue(), datePicker.getValue(), timeField.getText()));

        Button deleteAppointmentButton = new Button("Usuń wizytę"); // Tworzenie przycisku do usuwania wizyt
        // Ustawienie akcji dla przycisku usuwającego wizytę
        deleteAppointmentButton.setOnAction(e -> {
            String selectedId = appointmentIdComboBox.getValue(); // Pobranie wybranego ID wizyty
            if (selectedId != null) { // Sprawdzenie, czy ID zostało wybrane
                deleteAppointment(selectedId); // Usunięcie wybranej wizyty
            } else {
                System.out.println("Proszę wybrać ID wizyty do usunięcia."); // Komunikat, jeśli nie wybrano ID
            }
        });

        Button backButton = new Button("Powrót"); // Tworzenie przycisku powrotu
        backButton.setOnAction(e -> goBack()); // Ustawienie akcji dla przycisku powrotu

        Label appointmentIdLabel = new Label("Wybierz ID wizyty do usunięcia:"); // Tworzenie etykiety

        // Dodawanie elementów do układu
        layout.getChildren().addAll(
                clientComboBox,
                datePicker,
                timeField,
                addAppointmentButton,
                appointmentIdLabel,
                appointmentIdComboBox,
                deleteAppointmentButton,
                backButton
        );

        loadAppointmentIds(); // Ładowanie ID wizyt do rozwijanego menu

        Scene scene = new Scene(layout, 400, 300); // Tworzenie sceny z układem
        stage.setScene(scene); // Ustawienie sceny na scenie
        stage.show(); // Wyświetlenie sceny
    }

    // Metoda do ładowania klientów do rozwijanego menu
    private void loadClients(ComboBox<String> clientComboBox) {
        BazaDanych db = new BazaDanych(); // Tworzenie obiektu bazy danych
        Connection conn = null; // Deklaracja zmiennej połączenia
        PreparedStatement pstmt = null; // Deklaracja zmiennej do przygotowanego zapytania
        ResultSet rs = null; // Deklaracja zmiennej dla wyników zapytania

        try {
            conn = db.connect(); // Nawiązywanie połączenia z bazą danych
            String sql = "SELECT Imie, Nazwisko FROM Klienci"; // Zapytanie SQL
            pstmt = conn.prepareStatement(sql); // Przygotowanie zapytania
            rs = pstmt.executeQuery(); // Wykonanie zapytania

            while (rs.next()) { // Przetwarzanie wyników zapytania
                String imie = rs.getString("Imie"); // Pobranie imienia klienta
                String nazwisko = rs.getString("Nazwisko"); // Pobranie nazwiska klienta
                clientComboBox.getItems().add(imie + " " + nazwisko); // Dodanie klienta do rozwijanego menu
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas wczytywania klientów: " + e.getMessage()); // Obsługa błędów SQL
        } finally {
            try {
                if (rs != null) rs.close(); // Zamknięcie zbioru wyników
                if (pstmt != null) pstmt.close(); // Zamknięcie przygotowanego zapytania
                if (conn != null) conn.close(); // Zamknięcie połączenia
            } catch (SQLException e) {
                System.out.println("Błąd podczas zamykania połączenia: " + e.getMessage()); // Obsługa błędów SQL przy zamykaniu
            }
        }
    }

    // Metoda do dodawania nowej wizyty
    private void addAppointment(String client, LocalDate date, String timeString) {
        if (client == null || client.split(" ").length != 2) { // Sprawdzenie poprawności danych klienta
            showAlert("Błąd", "Wybierz prawidłowego klienta."); // Wyświetlenie alertu w przypadku błędu
            return; // Zakończenie metody w przypadku błędu
        }

        try {
            LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm")); // Parsowanie czasu z formatu tekstowego
            java.sql.Time sqlTime = java.sql.Time.valueOf(time); // Konwersja czasu na format SQL

            BazaDanych db = new BazaDanych(); // Tworzenie obiektu bazy danych
            try (Connection conn = db.connect()) { // Nawiązywanie połączenia z bazą danych
                String checkSql = "SELECT * FROM Wizyty WHERE Data = ? AND Godzina = ?"; // Zapytanie SQL do sprawdzenia dostępności terminu
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) { // Przygotowanie zapytania
                    checkStmt.setDate(1, java.sql.Date.valueOf(date)); // Ustawienie daty w zapytaniu
                    checkStmt.setTime(2, sqlTime); // Ustawienie czasu w zapytaniu

                    try (ResultSet rs = checkStmt.executeQuery()) { // Wykonanie zapytania
                        if (!rs.next()) { // Sprawdzenie, czy termin jest dostępny
                            String insertSql = "INSERT INTO Wizyty (Imie, Nazwisko, Data, Godzina) VALUES (?, ?, ?, ?)"; // Zapytanie SQL do dodania wizyty
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) { // Przygotowanie zapytania
                                String[] clientParts = client.split(" "); // Rozdzielenie danych klienta na imię i nazwisko
                                insertStmt.setString(1, clientParts[0]); // Ustawienie imienia w zapytaniu
                                insertStmt.setString(2, clientParts[1]); // Ustawienie nazwiska w zapytaniu
                                insertStmt.setDate(3, java.sql.Date.valueOf(date)); // Ustawienie daty w zapytaniu
                                insertStmt.setTime(4, sqlTime); // Ustawienie czasu w zapytaniu
                                insertStmt.executeUpdate(); // Wykonanie zapytania

                                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) { // Pobranie wygenerowanego klucza (ID wizyty)
                                    if (generatedKeys.next()) { // Sprawdzenie, czy klucz został wygenerowany
                                        int newAppointmentId = generatedKeys.getInt(1); // Pobranie ID nowej wizyty
                                        showAlert("Wizyta Dodana", "Wizyta została dodana. ID: " + newAppointmentId); // Wyświetlenie alertu z informacją o dodaniu wizyty
                                        loadAppointmentIds(); // Ponowne ładowanie ID wizyt
                                    }
                                }
                            }
                        } else {
                            showAlert("Termin zajęty", "Wybrany termin jest już zajęty. Proszę wybrać inny termin."); // Wyświetlenie alertu, jeśli termin jest zajęty
                        }
                    }
                }
            }
        } catch (DateTimeParseException e) {
            showAlert("Błąd formatu czasu", "Użyj formatu HH:mm, np. '15:30'."); // Obsługa błędów parsowania czasu
        } catch (SQLException e) {
            showAlert("Błąd SQL", "Wystąpił błąd podczas dodawania wizyty: " + e.getMessage()); // Obsługa błędów SQL
        }
    }

    // Metoda do usuwania wizyty
    private void deleteAppointment(String selectedIdString) {
        int IDwizyty = Integer.parseInt(selectedIdString.split(" - ")[0]); // Parsowanie ID wizyty z wybranego tekstu

        BazaDanych db = new BazaDanych(); // Tworzenie obiektu bazy danych
        try (Connection conn = db.connect(); // Nawiązywanie połączenia z bazą danych
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Wizyty WHERE IDwizyty = ?")) { // Przygotowanie zapytania SQL do usunięcia wizyty
            pstmt.setInt(1, IDwizyty); // Ustawienie ID wizyty w zapytaniu
            int rowsDeleted = pstmt.executeUpdate(); // Wykonanie zapytania i zwrócenie liczby usuniętych wierszy
            if (rowsDeleted > 0) { // Sprawdzenie, czy usunięto wiersze
                showAlert("Wizyta Usunięta", "Wizyta o ID: " + IDwizyty + " została usunięta."); // Wyświetlenie alertu o usunięciu wizyty
                loadAppointmentIds(); // Ponowne ładowanie ID wizyt
            } else {
                showAlert("Nie znaleziono Wizyty", "Nie znaleziono wizyty o podanym ID."); // Wyświetlenie alertu, jeśli nie znaleziono wizyty
            }
        } catch (SQLException e) {
            showAlert("Błąd SQL", "Błąd podczas usuwania wizyty: " + e.getMessage()); // Obsługa błędów SQL
        }
    }

    // Metoda do ładowania ID wizyt
    private void loadAppointmentIds() {
        BazaDanych db = new BazaDanych(); // Tworzenie obiektu bazy danych
        try (Connection conn = db.connect(); // Nawiązywanie połączenia z bazą danych
             PreparedStatement pstmt = conn.prepareStatement("SELECT IDwizyty, Imie, Nazwisko FROM Wizyty"); // Przygotowanie zapytania SQL
             ResultSet rs = pstmt.executeQuery()) { // Wykonanie zapytania

            appointmentIdComboBox.getItems().clear(); // Wyczyszczenie obecnych elementów w rozwijanym menu
            while (rs.next()) { // Przetwarzanie wyników zapytania
                int id = rs.getInt("IDwizyty"); // Pobranie ID wizyty
                String imie = rs.getString("Imie"); // Pobranie imienia klienta
                String nazwisko = rs.getString("Nazwisko"); // Pobranie nazwiska klienta
                appointmentIdComboBox.getItems().add(id + " - " + imie + " " + nazwisko); // Dodanie ID i danych klienta do rozwijanego menu
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas ładowania ID wizyt: " + e.getMessage()); // Obsługa błędów SQL
        }
    }

    // Metoda do powrotu do poprzedniego widoku
    private void goBack() {
        if (previousView != null) { // Sprawdzenie, czy istnieje poprzedni widok
            previousView.showMenu(); // Wyświetlenie menu poprzedniego widoku
        }
    }

    // Metoda do wyświetlania alertów
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Tworzenie obiektu alertu
        alert.setTitle(title); // Ustawienie tytułu alertu
        alert.setHeaderText(null); // Ustawienie nagłówka alertu
        alert.setContentText(content); // Ustawienie treści alertu
        alert.showAndWait(); // Wyświetlenie alertu i oczekiwanie na reakcję użytkownika
    }
}
