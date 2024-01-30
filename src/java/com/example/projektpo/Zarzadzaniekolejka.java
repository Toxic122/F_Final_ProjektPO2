package com.example.projektpo;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Statement;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Zarzadzaniekolejka {

    private Stage stage;
    private View previousView;
    public Zarzadzaniekolejka(Stage stage, View previousView) {
        this.stage = stage;
        this.previousView = previousView;
    }
    public void showQueueManagementUI() {
        VBox layout = new VBox(10);
        layout.setSpacing(10);

        ComboBox<String> wizytyComboBox = new ComboBox<>();
        zaladujWizyty(wizytyComboBox);

        Button dodajButton = new Button("Dodaj do kolejki");
        Button usunButton = new Button("Usuń z kolejki");
        Button wyswietlButton = new Button("Wyświetl kolejkę");
        Button backButton = new Button("Powrót");
        Label wynikLabel = new Label();

        dodajButton.setOnAction(e -> {
            String wybranaWizyta = wizytyComboBox.getValue();
            if (wybranaWizyta != null && !wybranaWizyta.isEmpty()) {
                String[] czesci = wybranaWizyta.split(" - ");
                if (czesci.length > 1) {
                    String[] imieNazwisko = czesci[1].split(" ", 2);
                    if (imieNazwisko.length == 2) {
                        String imie = imieNazwisko[0];
                        String nazwisko = imieNazwisko[1];
                        dodajDoKolejki(imie, nazwisko); // Bezpośrednie wywołanie metody
                        wynikLabel.setText("Dodano do kolejki: " + imie + " " + nazwisko);
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




        usunButton.setOnAction(e -> {
            String wybranaWizyta = wizytyComboBox.getValue();
            if (wybranaWizyta != null && !wybranaWizyta.isEmpty()) {
                String[] czesci = wybranaWizyta.split(" - ");
                if (czesci.length > 1) {
                    String[] imieNazwisko = czesci[1].split(" ", 2);
                    if (imieNazwisko.length == 2) {
                        String imie = imieNazwisko[0];
                        String nazwisko = imieNazwisko[1];
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

        wyswietlButton.setOnAction(e -> {
            wyswietlKolejke(); // Wywołanie metody do wyświetlania kolejki
        });
        backButton.setOnAction(e -> {
            // Tutaj umieść kod obsługujący powrót do poprzedniego widoku
            // Na przykład:
            View previousView = new View(stage); // Utwórz instancję poprzedniego widoku (zmień na właściwą klasę, jeśli potrzebujesz innej)
            previousView.showMenu(); // Wywołaj metodę wyświetlającą poprzedni widok
        });


        layout.getChildren().addAll(wizytyComboBox, dodajButton, usunButton, wyswietlButton, wynikLabel, backButton);

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void zaladujWizyty(ComboBox<String> comboBox) {
        BazaDanych db = new BazaDanych();
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT IDwizyty, Imie, Nazwisko FROM Wizyty");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String wizyta = rs.getInt("IDwizyty") + " - " + rs.getString("Imie") + " " + rs.getString("Nazwisko");
                comboBox.getItems().add(wizyta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void dodajDoKolejki(String imie, String nazwisko) {
        BazaDanych db = new BazaDanych();
        try (Connection conn = db.connect();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM Kolejka WHERE Imie = ? AND Nazwisko = ?")) {

            checkStmt.setString(1, imie);
            checkStmt.setString(2, nazwisko);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                pokazAlert("Klient już w kolejce", "Klient " + imie + " " + nazwisko + " jest już w kolejce.");
            } else {
                try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Kolejka (Imie, Nazwisko) VALUES (?, ?)")) {
                    pstmt.setString(1, imie);
                    pstmt.setString(2, nazwisko);
                    pstmt.executeUpdate();
                    pokazAlert("Dodano do kolejki", "Dodano do kolejki: " + imie + " " + nazwisko);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            pokazAlert("Błąd", "Błąd podczas dodawania do kolejki: " + e.getMessage());
        }
    }
    public void usunZKolejki(String imie, String nazwisko, Label wynikLabel) {
        BazaDanych db = new BazaDanych();
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Kolejka WHERE Imie = ? AND Nazwisko = ?")) {
            pstmt.setString(1, imie);
            pstmt.setString(2, nazwisko);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                pokazAlert("Usunięto z kolejki", "Usunięto z kolejki: " + imie + " " + nazwisko);
            } else {
                pokazAlert("Nie znaleziono osoby", "Nie znaleziono osoby w kolejce: " + imie + " " + nazwisko);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            pokazAlert("Błąd", "Błąd podczas usuwania z kolejki: " + e.getMessage());
        }
    }
    public void wyswietlKolejke() {
        BazaDanych db = new BazaDanych();
        StringBuilder wyniki = new StringBuilder();
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Kolejka")) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                wyniki.append("ID: ").append(rs.getInt("IDkolejki"))
                        .append(", Imię: ").append(rs.getString("Imie"))
                        .append(", Nazwisko: ").append(rs.getString("Nazwisko")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            pokazAlert("Błąd", "Błąd podczas wyświetlania kolejki: " + e.getMessage());
            return;
        }

        if (wyniki.length() == 0) {
            pokazAlert("Kolejka pusta", "Obecnie w kolejce nie ma klientów.");
        } else {
            pokazAlert("Kolejka", wyniki.toString());
        }
    }

    private void pokazAlert(String tytul, String tresc) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(tytul);
        alert.setHeaderText(null);
        alert.setContentText(tresc);
        alert.showAndWait();
    }

    private void goBack() {
        if (previousView != null) {
            previousView.showMenu(); // Przeniesienie użytkownika z powrotem do menu głównego
        }
    }



    // Metody dodajDoKolejki, usunZKolejki, wyswietlKolejke...
}
