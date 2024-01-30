package com.example.projektpo; // Definicja pakietu

import javafx.scene.Scene; // Importowanie klasy Scene z JavaFX
import javafx.scene.control.Button; // Importowanie klasy Button z JavaFX
import javafx.scene.control.ComboBox; // Importowanie klasy ComboBox z JavaFX
import javafx.scene.control.Label; // Importowanie klasy Label z JavaFX
import javafx.scene.layout.VBox; // Importowanie klasy VBox z JavaFX
import javafx.stage.Stage; // Importowanie klasy Stage z JavaFX
import java.sql.Statement; // Importowanie klasy Statement z Java SQL
import javafx.scene.control.Alert; // Importowanie klasy Alert z JavaFX

import java.sql.Connection; // Importowanie klasy Connection z Java SQL
import java.sql.PreparedStatement; // Importowanie klasy PreparedStatement z Java SQL
import java.sql.ResultSet; // Importowanie klasy ResultSet z Java SQL
import java.sql.SQLException; // Importowanie klasy SQLException z Java SQL

public class Zarzadzaniekolejka { // Definicja klasy publicznej Zarzadzaniekolejka

    private Stage stage; // Deklaracja prywatnego pola stage typu Stage
    private View previousView; // Deklaracja prywatnego pola previousView typu View
    public Zarzadzaniekolejka(Stage stage, View previousView) { // Konstruktor klasy z parametrami Stage i View
        this.stage = stage; // Przypisanie wartości do pola stage
        this.previousView = previousView; // Przypisanie wartości do pola previousView
    }
    public void showQueueManagementUI() { // Metoda do wyświetlenia interfejsu zarządzania kolejką
        VBox layout = new VBox(10); // Utworzenie obiektu VBox z odstępem 10
        layout.setSpacing(10); // Ustawienie odstępu między elementami w VBox

        ComboBox<String> wizytyComboBox = new ComboBox<>(); // Utworzenie rozwijanej listy ComboBox
        zaladujWizyty(wizytyComboBox); // Wywołanie metody do załadowania danych do ComboBox

        Button dodajButton = new Button("Dodaj do kolejki"); // Utworzenie przycisku "Dodaj do kolejki"
        Button usunButton = new Button("Usuń z kolejki"); // Utworzenie przycisku "Usuń z kolejki"
        Button wyswietlButton = new Button("Wyświetl kolejkę"); // Utworzenie przycisku "Wyświetl kolejkę"
        Button backButton = new Button("Powrót"); // Utworzenie przycisku "Powrót"
        Label wynikLabel = new Label(); // Utworzenie etykiety do wyświetlania wyników

        // Ustawienie zachowania po naciśnięciu przycisku dodajButton
        dodajButton.setOnAction(e -> {
            String wybranaWizyta = wizytyComboBox.getValue(); // Pobranie wybranej wartości z ComboBox
            // Sprawdzenie czy wybrana wizyta jest niepusta i istnieje
            if (wybranaWizyta != null && !wybranaWizyta.isEmpty()) {
                // Rozdzielenie ciągu wybranej wizyty na części
                String[] czesci = wybranaWizyta.split(" - ");
                // Sprawdzenie czy podzielony ciąg ma więcej niż jedną część
                if (czesci.length > 1) {
                    // Rozdzielenie drugiej części ciągu na imię i nazwisko
                    String[] imieNazwisko = czesci[1].split(" ", 2);
                    // Sprawdzenie czy udało się podzielić na imię i nazwisko
                    if (imieNazwisko.length == 2) {
                        String imie = imieNazwisko[0]; // Przypisanie imienia
                        String nazwisko = imieNazwisko[1]; // Przypisanie nazwiska
                        // Wywołanie metody dodającej do kolejki
                        dodajDoKolejki(imie, nazwisko);
                        // Ustawienie tekstu w wynikLabel
                        wynikLabel.setText("Dodano do kolejki: " + imie + " " + nazwisko);
                    } else {
                        // Ustawienie tekstu błędu w wynikLabel
                        wynikLabel.setText("Błąd formatu danych wizyty.");
                    }
                } else {
                    wynikLabel.setText("Błąd formatu danych wizyty.");
                }
            } else {
                wynikLabel.setText("Proszę wybrać wizytę.");
            }
        });

        // Ustawienie zachowania po naciśnięciu przycisku usunButton
        usunButton.setOnAction(e -> {
            String wybranaWizyta = wizytyComboBox.getValue(); // Pobranie wybranej wartości z ComboBox
            // Sprawdzenie czy wybrana wizyta jest niepusta i istnieje
            if (wybranaWizyta != null && !wybranaWizyta.isEmpty()) {
                // Rozdzielenie ciągu wybranej wizyty na części
                String[] czesci = wybranaWizyta.split(" - ");
                // Sprawdzenie czy podzielony ciąg ma więcej niż jedną część
                if (czesci.length > 1) {
                    // Rozdzielenie drugiej części ciągu na imię i nazwisko
                    String[] imieNazwisko = czesci[1].split(" ", 2);
                    // Sprawdzenie czy udało się podzielić na imię i nazwisko
                    if (imieNazwisko.length == 2) {
                        String imie = imieNazwisko[0]; // Przypisanie imienia
                        String nazwisko = imieNazwisko[1]; // Przypisanie nazwiska
                        // Wywołanie metody usuwającej z kolejki
                        usunZKolejki(imie, nazwisko, wynikLabel);
                    } else {
                        wynikLabel.setText("Błąd formatu danych wizyty.");
                    }
                } else {
                    wynikLabel.setText("Błąd formatu danych wizyty.");
                }
            } else {
                wynikLabel.setText("Proszę wybrać wizytę.");
            }
        });

        // Ustawienie zachowania po naciśnięciu przycisku wyswietlButton
        wyswietlButton.setOnAction(e -> {
            wyswietlKolejke(); // Wywołanie metody do wyświetlenia kolejki
        });
        // Ustawienie zachowania po naciśnięciu przycisku backButton
        backButton.setOnAction(e -> {
            // Wywołanie metody przenoszącej z powrotem do poprzedniego widoku
            if (previousView != null) {
                previousView.showMenu();
            }
        });

        // Dodanie elementów do layoutu
        layout.getChildren().addAll(wizytyComboBox, dodajButton, usunButton, wyswietlButton, wynikLabel, backButton);

        // Ustawienie i wyświetlenie sceny
        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.show();
    }

    // Metoda do załadowania danych do ComboBox
    private void zaladujWizyty(ComboBox<String> comboBox) {
        BazaDanych db = new BazaDanych(); // Utworzenie obiektu bazy danych
        // Próba połączenia z bazą danych i wykonania zapytania
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT IDwizyty, Imie, Nazwisko FROM Wizyty");
             ResultSet rs = pstmt.executeQuery()) {

            // Iteracja przez wyniki zapytania
            while (rs.next()) {
                // Utworzenie stringa z informacjami o wizycie i dodanie do ComboBox
                String wizyta = rs.getInt("IDwizyty") + " - " + rs.getString("Imie") + " " + rs.getString("Nazwisko");
                comboBox.getItems().add(wizyta);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Wyświetlenie błędu w przypadku wyjątku SQL
        }
    }

    // Metoda dodająca osobę do kolejki
    public void dodajDoKolejki(String imie, String nazwisko) {
        BazaDanych db = new BazaDanych(); // Utworzenie obiektu bazy danych
        // Próba połączenia z bazą danych i wykonania zapytania
        try (Connection conn = db.connect();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM Kolejka WHERE Imie = ? AND Nazwisko = ?")) {

            checkStmt.setString(1, imie); // Ustawienie parametru imienia w zapytaniu
            checkStmt.setString(2, nazwisko); // Ustawienie parametru nazwiska w zapytaniu
            ResultSet rs = checkStmt.executeQuery(); // Wykonanie zapytania

            // Sprawdzenie czy osoba jest już w kolejce
            if (rs.next()) {
                // Wyświetlenie alertu o obecności osoby w kolejce
                pokazAlert("Klient już w kolejce", "Klient " + imie + " " + nazwisko + " jest już w kolejce.");
            } else {
                // Próba dodania osoby do kolejki
                try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Kolejka (Imie, Nazwisko) VALUES (?, ?)")) {
                    pstmt.setString(1, imie); // Ustawienie parametru imienia w zapytaniu
                    pstmt.setString(2, nazwisko); // Ustawienie parametru nazwiska w zapytaniu
                    pstmt.executeUpdate(); // Wykonanie zapytania
                    // Wyświetlenie alertu o dodaniu do kolejki
                    pokazAlert("Dodano do kolejki", "Dodano do kolejki: " + imie + " " + nazwisko);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Wyświetlenie błędu w przypadku wyjątku SQL
            // Wyświetlenie alertu o błędzie
            pokazAlert("Błąd", "Błąd podczas dodawania do kolejki: " + e.getMessage());
        }
    }

    // Metoda usuwająca osobę z kolejki
    public void usunZKolejki(String imie, String nazwisko, Label wynikLabel) {
        BazaDanych db = new BazaDanych(); // Utworzenie obiektu bazy danych
        // Próba połączenia z bazą danych i wykonania zapytania
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Kolejka WHERE Imie = ? AND Nazwisko = ?")) {
            pstmt.setString(1, imie); // Ustawienie parametru imienia w zapytaniu
            pstmt.setString(2, nazwisko); // Ustawienie parametru nazwiska w zapytaniu
            int affectedRows = pstmt.executeUpdate(); // Wykonanie zapytania i zapisanie liczby zmienionych rekordów

            // Sprawdzenie czy usunięto rekord
            if (affectedRows > 0) {
                // Wyświetlenie alertu o usunięciu z kolejki
                pokazAlert("Usunięto z kolejki", "Usunięto z kolejki: " + imie + " " + nazwisko);
            } else {
                // Wyświetlenie alertu o braku osoby w kolejce
                pokazAlert("Nie znaleziono osoby", "Nie znaleziono osoby w kolejce: " + imie + " " + nazwisko);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Wyświetlenie błędu w przypadku wyjątku SQL
            // Wyświetlenie alertu o błędzie
            pokazAlert("Błąd", "Błąd podczas usuwania z kolejki: " + e.getMessage());
        }
    }

    // Metoda wyświetlająca aktualny stan kolejki
    public void wyswietlKolejke() {
        BazaDanych db = new BazaDanych(); // Utworzenie obiektu bazy danych
        StringBuilder wyniki = new StringBuilder(); // Utworzenie StringBuilder do składania wyników
        // Próba połączenia z bazą danych i wykonania zapytania
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Kolejka")) {
            ResultSet rs = pstmt.executeQuery(); // Wykonanie zapytania

            // Iteracja przez wyniki zapytania
            while (rs.next()) {
                // Dodawanie informacji o osobach w kolejce do wyników
                wyniki.append("ID: ").append(rs.getInt("IDkolejki"))
                        .append(", Imię: ").append(rs.getString("Imie"))
                        .append(", Nazwisko: ").append(rs.getString("Nazwisko")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Wyświetlenie błędu w przypadku wyjątku SQL
            // Wyświetlenie alertu o błędzie
            pokazAlert("Błąd", "Błąd podczas wyświetlania kolejki: " + e.getMessage());
            return;
        }

        // Sprawdzenie czy wyniki są puste
        if (wyniki.length() == 0) {
            // Wyświetlenie alertu o pustej kolejce
            pokazAlert("Kolejka pusta", "Obecnie w kolejce nie ma klientów.");
        } else {
            // Wyświetlenie alertu z wynikami
            pokazAlert("Kolejka", wyniki.toString());
        }
    }

    // Metoda do wyświetlania alertów
    private void pokazAlert(String tytul, String tresc) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Utworzenie obiektu alertu
        alert.setTitle(tytul); // Ustawienie tytułu alertu
        alert.setHeaderText(null); // Ustawienie nagłówka alertu na null
        alert.setContentText(tresc); // Ustawienie treści alertu
        alert.showAndWait(); // Wyświetlenie alertu i czekanie na reakcję użytkownika
    }

    // Metoda do powrotu do poprzedniego widoku
    private void goBack() {
        if (previousView != null) {
            previousView.showMenu(); // Wywołanie metody wyświetlającej menu główne
        }
    }

    // Metody dodajDoKolejki, usunZKolejki, wyswietlKolejke...
}
