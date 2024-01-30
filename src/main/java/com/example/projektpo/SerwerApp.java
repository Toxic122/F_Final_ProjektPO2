package com.example.projektpo; // Definiuje pakiet, w którym znajduje się klasa, co pomaga w organizacji projektu.

// Import klas z JavaFX potrzebnych do stworzenia interfejsu użytkownika.
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// Import klas sieciowych do uzyskania adresu IP.
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SerwerApp extends Application { // Rozszerza klasę Application, co umożliwia tworzenie aplikacji JavaFX.
    private Serwer serwer; // Zmienna dla instancji serwera.
    private Text statusText; // Tekst wyświetlający status serwera w interfejsie użytkownika.

    @Override
    public void start(Stage primaryStage) { // Metoda start, która jest punktem wejścia dla aplikacji JavaFX.
        serwer = new Serwer(8080, 10); // Inicjalizacja serwera z określonym portem i rozmiarem puli wątków.
        VBox root = new VBox(10); // Utworzenie layoutu VBox z odstępem między elementami.
        root.setAlignment(Pos.CENTER); // Wyśrodkowanie elementów w layout'cie.

        Button startButton = new Button("Uruchom serwer"); // Przycisk do uruchamiania serwera.
        Button stopButton = new Button("Zatrzymaj serwer"); // Przycisk do zatrzymywania serwera.
        statusText = new Text("Status: Wyłączony"); // Tekst początkowy informujący o statusie serwera.

        startButton.setOnAction(e -> startServer()); // Ustawienie akcji dla przycisku start: uruchomienie serwera.
        stopButton.setOnAction(e -> stopServer()); // Ustawienie akcji dla przycisku stop: zatrzymanie serwera.

        root.getChildren().addAll(startButton, stopButton, statusText); // Dodanie elementów do layoutu.

        primaryStage.setScene(new Scene(root, 300, 150)); // Ustawienie sceny dla głównego okna aplikacji.
        primaryStage.setTitle("Serwer"); // Ustawienie tytułu okna.
        primaryStage.show(); // Wyświetlenie okna aplikacji.
    }

    private void startServer() { // Metoda uruchamiająca serwer w nowym wątku.
        new Thread(() -> {
            serwer.startServer(); // Uruchomienie serwera.
            updateStatus(); // Aktualizacja statusu serwera w interfejsie użytkownika.
        }).start(); // Start nowego wątku.
        updateStatus(); // Aktualizacja statusu również poza wątkiem, aby odzwierciedlić natychmiastową zmianę.
    }

    private void stopServer() { // Metoda zatrzymująca serwer.
        serwer.stopServer(); // Zatrzymanie serwera.
        updateStatus(); // Aktualizacja statusu serwera w interfejsie użytkownika.
    }

    private void updateStatus() { // Metoda aktualizująca tekst statusu w interfejsie użytkownika.
        try {
            InetAddress ip = InetAddress.getLocalHost(); // Pobranie lokalnego adresu IP.
            // Ustawienie tekstu statusu w zależności od tego, czy serwer jest uruchomiony.
            String status = serwer.isRunning() ? "Uruchomiony na adresie: " + ip.getHostAddress() + ", port: 12345" : "Wyłączony";
            statusText.setText("Status: " + status); // Aktualizacja tekstu statusu.
        } catch (UnknownHostException e) { // Obsługa wyjątku, gdy nie można uzyskać adresu IP.
            statusText.setText("Błąd: Nie można uzyskać adresu IP"); // Informacja o błędzie w statusie.
        }
    }

    public static void main(String[] args) { // Główna metoda uruchamiająca aplikację.
        launch(args); // Uruchomienie aplikacji JavaFX.
    }
}
