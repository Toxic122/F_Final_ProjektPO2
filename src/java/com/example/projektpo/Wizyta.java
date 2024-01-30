package com.example.projektpo;

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

public class Wizyta {
    private Stage stage;
    private View previousView;
    private ComboBox<String> appointmentIdComboBox = new ComboBox<>();

    public Wizyta(Stage stage, View previousView) {
        this.stage = stage;
        this.previousView = previousView;
    }

    public void showManageAppointments() {
        VBox layout = new VBox(10);

        ComboBox<String> clientComboBox = new ComboBox<>();
        loadClients(clientComboBox);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField timeField = new TextField();
        timeField.setPromptText("Wpisz Godzinę. Użyj formatu HH:mm");

        Button addAppointmentButton = new Button("Dodaj wizytę");
        addAppointmentButton.setOnAction(e -> addAppointment(clientComboBox.getValue(), datePicker.getValue(), timeField.getText()));

        Button deleteAppointmentButton = new Button("Usuń wizytę");
        deleteAppointmentButton.setOnAction(e -> {
            String selectedId = appointmentIdComboBox.getValue();
            if (selectedId != null) {
                deleteAppointment(selectedId);
            } else {
                System.out.println("Proszę wybrać ID wizyty do usunięcia.");
            }
        });

        Button backButton = new Button("Powrót");
        backButton.setOnAction(e -> goBack());

        Label appointmentIdLabel = new Label("Wybierz ID wizyty do usunięcia:");

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

        loadAppointmentIds();

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void loadClients(ComboBox<String> clientComboBox) {
        BazaDanych db = new BazaDanych();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = db.connect();
            String sql = "SELECT Imie, Nazwisko FROM Klienci";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String imie = rs.getString("Imie");
                String nazwisko = rs.getString("Nazwisko");
                clientComboBox.getItems().add(imie + " " + nazwisko);
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas wczytywania klientów: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Błąd podczas zamykania połączenia: " + e.getMessage());
            }
        }
    }

    private void addAppointment(String client, LocalDate date, String timeString) {
        if (client == null || client.split(" ").length != 2) {
            showAlert("Błąd", "Wybierz prawidłowego klienta.");
            return;
        }

        try {
            LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
            java.sql.Time sqlTime = java.sql.Time.valueOf(time);

            BazaDanych db = new BazaDanych();
            try (Connection conn = db.connect()) {
                String checkSql = "SELECT * FROM Wizyty WHERE Data = ? AND Godzina = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setDate(1, java.sql.Date.valueOf(date));
                    checkStmt.setTime(2, sqlTime);

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {
                            String insertSql = "INSERT INTO Wizyty (Imie, Nazwisko, Data, Godzina) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                                String[] clientParts = client.split(" ");
                                insertStmt.setString(1, clientParts[0]);
                                insertStmt.setString(2, clientParts[1]);
                                insertStmt.setDate(3, java.sql.Date.valueOf(date));
                                insertStmt.setTime(4, sqlTime);
                                insertStmt.executeUpdate();

                                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                                    if (generatedKeys.next()) {
                                        int newAppointmentId = generatedKeys.getInt(1);
                                        showAlert("Wizyta Dodana", "Wizyta została dodana. ID: " + newAppointmentId);
                                        loadAppointmentIds(); // Reload IDs
                                    }
                                }
                            }
                        } else {
                            showAlert("Termin zajęty", "Wybrany termin jest już zajęty. Proszę wybrać inny termin.");
                        }
                    }
                }
            }
        } catch (DateTimeParseException e) {
            showAlert("Błąd formatu czasu", "Użyj formatu HH:mm, np. '15:30'.");
        } catch (SQLException e) {
            showAlert("Błąd SQL", "Wystąpił błąd podczas dodawania wizyty: " + e.getMessage());
        }
    }

    private void deleteAppointment(String selectedIdString) {
        int IDwizyty = Integer.parseInt(selectedIdString.split(" - ")[0]);

        BazaDanych db = new BazaDanych();
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Wizyty WHERE IDwizyty = ?")) {
            pstmt.setInt(1, IDwizyty);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Wizyta Usunięta", "Wizyta o ID: " + IDwizyty + " została usunięta.");
                loadAppointmentIds(); // Reload IDs
            } else {
                showAlert("Nie znaleziono Wizyty", "Nie znaleziono wizyty o podanym ID.");
            }
        } catch (SQLException e) {
            showAlert("Błąd SQL", "Błąd podczas usuwania wizyty: " + e.getMessage());
        }
    }

    private void loadAppointmentIds() {
        BazaDanych db = new BazaDanych();
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT IDwizyty, Imie, Nazwisko FROM Wizyty");
             ResultSet rs = pstmt.executeQuery()) {

            appointmentIdComboBox.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt("IDwizyty");
                String imie = rs.getString("Imie");
                String nazwisko = rs.getString("Nazwisko");
                appointmentIdComboBox.getItems().add(id + " - " + imie + " " + nazwisko);
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas ładowania ID wizyt: " + e.getMessage());
        }
    }

    private void goBack() {
        if (previousView != null) {
            previousView.showMenu();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
