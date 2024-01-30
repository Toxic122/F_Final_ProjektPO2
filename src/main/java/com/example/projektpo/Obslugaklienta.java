package com.example.projektpo; // Definiuje przestrzeń nazw dla klasy, umożliwiając organizację kodu.

// Importy klas z JavaFX i JDBC potrzebnych do budowy interfejsu użytkownika i obsługi bazy danych.
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

public class Obslugaklienta { // Główna klasa obsługująca interfejs użytkownika dla zarządzania klientami.
    // Deklaracje pól klasy do przechowywania referencji do sceny, poprzedniego widoku i tabeli z klientami.
    private Stage stage;
    private View previousView;
    private TableView<Klient> tableView;

    // Konstruktor klasy, inicjalizuje podstawowe elementy interfejsu użytkownika.
    public Obslugaklienta(Stage stage, View previousView) {
        this.stage = stage; // Przypisanie referencji do głównego okna aplikacji.
        this.previousView = previousView; // Przechowywanie widoku, z którego użytkownik przeszedł do obecnego widoku.
        this.tableView = new TableView<>(); // Inicjalizacja tabeli do wyświetlania danych klientów.
        setupTableView(); // Wywołanie metody konfigurującej tabelę.
    }

    // Metoda pozwalająca na zmianę widoku poprzedniego.
    public void setPreviousView(View view) {
        this.previousView = view; // Aktualizacja referencji do poprzedniego widoku.
    }

    // Metoda wyświetlająca interfejs zarządzania klientami.
    public void showClientManagement() {
        VBox layout = new VBox(10); // Tworzenie pionowego układu komponentów z odstępem 10 pikseli.
        layout.setSpacing(10); // Dodatkowe ustawienie odstępu między elementami.

        // Tworzenie przycisków i pól tekstowych z ustawieniami właściwości, takimi jak tekst zastępczy i kolor.
        Button addButton = new Button("Dodaj klienta");
        TextField clientInfoField = new TextField();
        clientInfoField.setPromptText("Podaj imię i nazwisko klienta do Dodania lub usunięcia, np. Jan Kowalski");
        clientInfoField.setStyle("-fx-prompt-text-fill: red;");
        Button editButton = new Button("Edytuj klienta");
        Button deleteButton = new Button("Usuń klienta");
        Button backButton = new Button("Powrót");

        TextField currentClientInfoField = new TextField();
        currentClientInfoField.setPromptText("Aktualne imię i nazwisko klienta");
        currentClientInfoField.setStyle("-fx-prompt-text-fill: red;");

        TextField newImieField = new TextField();
        newImieField.setPromptText("Nowe imię klienta");
        newImieField.setStyle("-fx-prompt-text-fill: red;");

        TextField newNazwiskoField = new TextField();
        newNazwiskoField.setPromptText("Nowe nazwisko klienta");
        newNazwiskoField.setStyle("-fx-prompt-text-fill: red;");

        // Ustawienie akcji, które mają być wykonane po kliknięciu przycisków.
        addButton.setOnAction(e -> addClient(clientInfoField.getText()));
        editButton.setOnAction(e -> editClient(currentClientInfoField.getText(), newImieField.getText(), newNazwiskoField.getText()));
        deleteButton.setOnAction(e -> deleteClient(clientInfoField.getText()));
        backButton.setOnAction(e -> goBack());

        // Dodawanie wszystkich elementów interfejsu użytkownika do głównego kontenera.
        layout.getChildren().addAll(addButton, deleteButton, clientInfoField, editButton, currentClientInfoField, newImieField, newNazwiskoField, backButton, tableView);

        Scene scene = new Scene(layout, 500, 400); // Tworzenie sceny z określonymi wymiarami.
        stage.setScene(scene); // Ustawienie sceny dla głównego okna aplikacji.
        stage.show(); // Wyświetlenie okna z interfejsem użytkownika.
    }

    // Metoda konfigurująca wygląd i działanie tabeli wyświetlającej dane klientów.
    private void setupTableView() {
        // Tworzenie i konfiguracja kolumn tabeli do wyświetlania imienia i nazwiska klientów.
        TableColumn<Klient, String> imieColumn = new TableColumn<>("Imię");
        imieColumn.setCellValueFactory(new PropertyValueFactory<>("imie"));

        TableColumn<Klient, String> nazwiskoColumn = new TableColumn<>("Nazwisko");
        nazwiskoColumn.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));

        tableView.getColumns().addAll(imieColumn, nazwiskoColumn); // Dodanie kolumn do tabeli.
        loadClients(); // Załadowanie danych klientów do tabeli.
    }

    // Metoda odpowiedzialna za wczytanie danych klientów z bazy danych i ich wyświetlenie.
    private void loadClients() {
        ObservableList<Klient> clients = FXCollections.observableArrayList(); // Lista do przechowywania danych klientów.
        BazaDanych bazaDanych = new BazaDanych(); // Utworzenie obiektu do połączenia z bazą danych.
        try (Connection conn = bazaDanych.connect(); // Nawiązanie połączenia z bazą danych.
             PreparedStatement pstmt = conn.prepareStatement("SELECT Imie, Nazwisko FROM Klienci"); // Przygotowanie zapytania SQL.
             ResultSet rs = pstmt.executeQuery()) { // Wykonanie zapytania i otrzymanie wyników.

            while (rs.next()) { // Przetwarzanie wyników zapytania.
                clients.add(new Klient(rs.getString("Imie"), rs.getString("Nazwisko"))); // Dodawanie danych klienta do listy.
            }
        } catch (SQLException e) { // Obsługa wyjątków związanych z bazą danych.
            System.out.println("Błąd ładowania klientów: " + e.getMessage());
        }
        tableView.setItems(clients); // Ustawienie danych dla tabeli.
    }

    // Metoda do dodawania nowego klienta do bazy danych na podstawie podanych informacji.
    private void addClient(String clientInfo) {
        BazaDanych bazaDanych = new BazaDanych(); // Utworzenie obiektu do połączenia z bazą danych.
        String sql = "INSERT INTO Klienci (Imie, Nazwisko) VALUES (?, ?)"; // Zapytanie SQL do dodania klienta.
        String[] infoParts = clientInfo.split(" "); // Rozdzielenie podanych informacji na imię i nazwisko.

        if (infoParts.length != 2) { // Walidacja podanych danych.
            showAlert("Błąd", "Nieprawidłowy format danych klienta. Oczekiwano: imie nazwisko");
            return;
        }

        try (Connection conn = bazaDanych.connect(); // Nawiązanie połączenia z bazą danych.
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Przygotowanie zapytania SQL.
            pstmt.setString(1, infoParts[0]); // Ustawienie imienia klienta.
            pstmt.setString(2, infoParts[1]); // Ustawienie nazwiska klienta.
            pstmt.executeUpdate(); // Wykonanie zapytania.
            showAlert("Sukces", "Dodano klienta: " + clientInfo); // Wyświetlenie informacji o sukcesie.
            loadClients(); // Odświeżenie listy klientów.
        } catch (SQLException e) { // Obsługa wyjątków SQL.
            showAlert("Błąd SQL", e.getMessage());
        }
    }

    // Metoda do edycji danych istniejącego klienta w bazie danych.
    private void editClient(String oldClientInfo, String newImie, String newNazwisko) {
        BazaDanych bazaDanych = new BazaDanych(); // Utworzenie obiektu do połączenia z bazą danych.
        String sql = "UPDATE Klienci SET Imie = ?, Nazwisko = ? WHERE Imie = ? AND Nazwisko = ?"; // Zapytanie SQL do aktualizacji danych klienta.
        String[] oldInfoParts = oldClientInfo.split(" "); // Rozdzielenie starych informacji klienta na imię i nazwisko.

        if (oldInfoParts.length != 2) { // Walidacja starych danych klienta.
            showAlert("Błąd", "Nieprawidłowy format danych klienta. Oczekiwano: imie nazwisko");
            return;
        }

        if (newImie.length() <= 0 || newNazwisko.length() <= 0) { // Walidacja nowych danych klienta.
            showAlert("Błąd", "Wprowadź poprawne dane.");
            return;
        }

        try (Connection conn = bazaDanych.connect(); // Nawiązanie połączenia z bazą danych.
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Przygotowanie zapytania SQL.
            pstmt.setString(1, newImie); // Ustawienie nowego imienia klienta.
            pstmt.setString(2, newNazwisko); // Ustawienie nowego nazwiska klienta.
            pstmt.setString(3, oldInfoParts[0]); // Ustawienie starego imienia dla warunku WHERE.
            pstmt.setString(4, oldInfoParts[1]); // Ustawienie starego nazwiska dla warunku WHERE.

            int rowsAffected = pstmt.executeUpdate(); // Wykonanie zapytania i sprawdzenie liczby zmodyfikowanych rekordów.
            if (rowsAffected > 0) { // Sprawdzenie, czy aktualizacja się powiodła.
                showAlert("Sukces", "Zaktualizowano dane klienta."); // Informacja o sukcesie.
                loadClients(); // Odświeżenie listy klientów.
            } else {
                showAlert("Błąd", "Nie znaleziono klienta o podanych danych."); // Informacja, gdy klient nie zostanie znaleziony.
            }
        } catch (SQLException e) { // Obsługa wyjątków SQL.
            showAlert("Błąd SQL", e.getMessage());
        }
    }

    // Metoda do usuwania klienta z bazy danych.
    private void deleteClient(String clientInfo) {
        BazaDanych bazaDanych = new BazaDanych(); // Utworzenie obiektu do połączenia z bazą danych.
        String sql = "DELETE FROM Klienci WHERE Imie = ? AND Nazwisko = ?"; // Zapytanie SQL do usunięcia klienta.
        String[] infoParts = clientInfo.split(" "); // Rozdzielenie informacji klienta na imię i nazwisko.

        if (infoParts.length != 2) { // Walidacja formatu danych klienta.
            showAlert("Błąd", "Nieprawidłowy format danych klienta. Oczekiwano: imie nazwisko");
            return;
        }

        try (Connection conn = bazaDanych.connect(); // Nawiązanie połączenia z bazą danych.
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Przygotowanie zapytania SQL.
            pstmt.setString(1, infoParts[0]); // Ustawienie imienia klienta dla warunku WHERE.
            pstmt.setString(2, infoParts[1]); // Ustawienie nazwiska klienta dla warunku WHERE.
            int rowsDeleted = pstmt.executeUpdate(); // Wykonanie zapytania i sprawdzenie liczby usuniętych rekordów.

            if (rowsDeleted > 0) { // Sprawdzenie, czy usunięcie się powiodło.
                showAlert("Sukces", "Usunięto klienta: " + clientInfo); // Informacja o sukcesie.
                loadClients(); // Odświeżenie listy klientów.
            } else {
                showAlert("Informacja", "Nie znaleziono klienta o podanych danych: " + clientInfo); // Informacja, gdy klient nie zostanie znaleziony.
            }
        } catch (SQLException e) { // Obsługa wyjątków SQL.
            showAlert("Błąd SQL", e.getMessage());
        }
    }

    // Metoda pozwalająca na powrót do poprzedniego widoku.
    private void goBack() {
        if (previousView != null) { // Sprawdzenie, czy istnieje poprzedni widok.
            previousView.showMenu(); // Wywołanie metody wyświetlającej poprzedni widok.
        }
    }

    // Metoda wyświetlająca komunikaty informacyjne.
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Utworzenie nowego alertu typu informacyjnego.
        alert.setTitle(title); // Ustawienie tytułu alertu.
        alert.setHeaderText(null); // Brak tekstu nagłówka.
        alert.setContentText(message); // Ustawienie treści wiadomości.
        alert.showAndWait(); // Wyświetlenie alertu i oczekiwanie na reakcję użytkownika.
    }

    // Klasa wewnętrzna reprezentująca klienta, używana do przechowywania danych klientów.
    public static class Klient {
        // Deklaracja właściwości dla imienia i nazwiska klienta, używając SimpleStringProperty dla obsługi przez JavaFX.
        private final SimpleStringProperty imie;
        private final SimpleStringProperty nazwisko;

        // Konstruktor klasy Klient, przyjmujący imię i nazwisko.
        public Klient(String imie, String nazwisko) {
            this.imie = new SimpleStringProperty(imie); // Inicjalizacja właściwości imienia.
            this.nazwisko = new SimpleStringProperty(nazwisko); // Inicjalizacja właściwości nazwiska.
        }

        // Metody dostępowe (gettery) do właściwości imienia i nazwiska.
        public String getImie() {
            return imie.get();
        }

        public String getNazwisko() {
            return nazwisko.get();
        }
    }
}
