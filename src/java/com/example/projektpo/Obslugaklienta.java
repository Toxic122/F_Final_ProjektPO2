package com.example.projektpo;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;

public class Obslugaklienta {
    private Stage stage;
    private View previousView;
    private TableView<Klient> tableView;

    public Obslugaklienta(Stage stage, View previousView) {
        this.stage = stage;
        this.previousView = previousView;
        this.tableView = new TableView<>();
        setupTableView();
    }

    public void setPreviousView(View view) {
        this.previousView = view;
    }

    public void showClientManagement() {
        VBox layout = new VBox(10);
        layout.setSpacing(10);

        Button addButton = new Button("Dodaj klienta");
        TextField clientInfoField = new TextField();
        clientInfoField.setPromptText("Podaj imię i nazwisko klienta do Dodania lub usunięcia, np. Jan Kowalski");
        clientInfoField.setStyle("-fx-prompt-text-fill: red;");
        Button editButton = new Button("Edytuj klienta");
        Button deleteButton = new Button("Usuń klienta");
        Button backButton = new Button("Powrót");

        TextField currentClientInfoField = new TextField();
        currentClientInfoField.setPromptText("Aktualne imię i nazwisko klienta ");
        currentClientInfoField.setStyle("-fx-prompt-text-fill: red;");

        TextField newImieField = new TextField();
        newImieField.setPromptText("Nowe imię klienta ");
        newImieField.setStyle("-fx-prompt-text-fill: red;");

        TextField newNazwiskoField = new TextField();
        newNazwiskoField.setPromptText("Nowe nazwisko klienta ");
        newNazwiskoField.setStyle("-fx-prompt-text-fill: red;");

        addButton.setOnAction(e -> addClient(clientInfoField.getText()));
        editButton.setOnAction(e -> editClient(currentClientInfoField.getText(), newImieField.getText(), newNazwiskoField.getText()));
        deleteButton.setOnAction(e -> deleteClient(clientInfoField.getText()));
        backButton.setOnAction(e -> goBack());

        layout.getChildren().addAll(addButton,deleteButton,clientInfoField, editButton,currentClientInfoField, newImieField, newNazwiskoField,  backButton,

                 tableView);

        Scene scene = new Scene(layout, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void setupTableView() {
        TableColumn<Klient, String> imieColumn = new TableColumn<>("Imię");
        imieColumn.setCellValueFactory(new PropertyValueFactory<>("imie"));

        TableColumn<Klient, String> nazwiskoColumn = new TableColumn<>("Nazwisko");
        nazwiskoColumn.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));

        tableView.getColumns().addAll(imieColumn, nazwiskoColumn);
        loadClients();
    }

    private void loadClients() {
        ObservableList<Klient> clients = FXCollections.observableArrayList();
        BazaDanych bazaDanych = new BazaDanych();
        try (Connection conn = bazaDanych.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT Imie, Nazwisko FROM Klienci");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                clients.add(new Klient(rs.getString("Imie"), rs.getString("Nazwisko")));
            }
        } catch (SQLException e) {
            System.out.println("Błąd ładowania klientów: " + e.getMessage());
        }
        tableView.setItems(clients);
    }

    private void addClient(String clientInfo) {
        BazaDanych bazaDanych = new BazaDanych();
        String sql = "INSERT INTO Klienci (Imie, Nazwisko) VALUES (?, ?)";
        String[] infoParts = clientInfo.split(" ");

        if (infoParts.length != 2) {
            showAlert("Błąd", "Nieprawidłowy format danych klienta. Oczekiwano: imie nazwisko");
            return;
        }

        try (Connection conn = bazaDanych.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, infoParts[0]);
            pstmt.setString(2, infoParts[1]);
            pstmt.executeUpdate();
            showAlert("Sukces", "Dodano klienta: " + clientInfo);
            loadClients(); // Odświeżenie listy klientów
        } catch (SQLException e) {
            showAlert("Błąd SQL", e.getMessage());
        }
    }

    private void editClient(String oldClientInfo, String newImie, String newNazwisko) {
        BazaDanych bazaDanych = new BazaDanych();
        String sql = "UPDATE Klienci SET Imie = ?, Nazwisko = ? WHERE Imie = ? AND Nazwisko = ?";
        String[] oldInfoParts = oldClientInfo.split(" ");

        if (oldInfoParts.length != 2) {
            showAlert("Błąd", "Kurwa");
            return;
        }

         if (newImie.length() <=0 || newNazwisko.length()<=0 ) {
             showAlert("Błąd", "Wprowadź poprawne dane.");
             return;

         }

        try (Connection conn = bazaDanych.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newImie);
            pstmt.setString(2, newNazwisko);
            pstmt.setString(3, oldInfoParts[0]);
            pstmt.setString(4, oldInfoParts[1]);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Sukces", "Zaktualizowano dane klienta.");
                loadClients(); // Odśwież listę klientów
            } else {
                showAlert("Błąd", "Nie znaleziono klienta o podanych danych.");
            }
        } catch (SQLException e) {
            showAlert("Błąd SQL", e.getMessage());
        }
    }

    private void deleteClient(String clientInfo) {
        BazaDanych bazaDanych = new BazaDanych();
        String sql = "DELETE FROM Klienci WHERE Imie = ? AND Nazwisko = ?";
        String[] infoParts = clientInfo.split(" ");

        if (infoParts.length != 2) {
            showAlert("Błąd", "Nieprawidłowy format danych klienta. Oczekiwano: imie nazwisko");
            return;
        }

        try (Connection conn = bazaDanych.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, infoParts[0]);
            pstmt.setString(2, infoParts[1]);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                showAlert("Sukces", "Usunięto klienta: " + clientInfo);
                loadClients(); // Odświeżenie listy klientów
            } else {
                showAlert("Informacja", "Nie znaleziono klienta o podanych danych: " + clientInfo);
            }
        } catch (SQLException e) {
            showAlert("Błąd SQL", e.getMessage());
        }
    }

    private void goBack() {
        if (previousView != null) {
            previousView.showMenu();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Klient {
        private final SimpleStringProperty imie;
        private final SimpleStringProperty nazwisko;

        public Klient(String imie, String nazwisko) {
            this.imie = new SimpleStringProperty(imie);
            this.nazwisko = new SimpleStringProperty(nazwisko);
        }

        public String getImie() {
            return imie.get();
        }

        public String getNazwisko() {
            return nazwisko.get();
        }
    }
}
