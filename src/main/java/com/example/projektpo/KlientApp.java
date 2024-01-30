package com.example.projektpo; // Deklaracja pakietu

import javafx.application.Application; // Import klasy bazowej Application z JavaFX
import javafx.geometry.Pos; // Import klasy Pos do zarządzania wyrównaniem elementów
import javafx.scene.Scene; // Import klasy Scene z JavaFX
import javafx.scene.control.Button; // Import klasy Button z JavaFX
import javafx.scene.layout.VBox; // Import klasy VBox z JavaFX do układania elementów w pionie
import javafx.scene.text.Text; // Import klasy Text z JavaFX
import javafx.stage.Stage; // Import klasy Stage z JavaFX, reprezentującej okno aplikacji

import java.io.*; // Import klas do obsługi wejścia/wyjścia
import java.net.Socket; // Import klasy Socket do obsługi połączeń sieciowych

public class KlientApp extends Application { // Klasa aplikacji dziedzicząca po Application z JavaFX
    private Socket socket; // Gniazdo do komunikacji z serwerem
    private PrintWriter out; // Strumień wyjściowy do wysyłania danych do serwera
    private BufferedReader in; // Buforowany strumień wejściowy do odbierania danych z serwera
    private final String serverAddress = "192.168.56.1"; // Adres IP serwera
    private final int serverPort = 12345; // Port serwera
    private Stage primaryStage; // Główne okno aplikacji

    @Override
    public void start(Stage primaryStage) { // Metoda uruchamiana przy starcie aplikacji
        this.primaryStage = primaryStage; // Przypisanie głównego okna aplikacji do zmiennej
        VBox root = new VBox(10); // Utworzenie kontenera VBox
        root.setAlignment(Pos.CENTER); // Ustawienie wyrównania elementów na środek

        Button connectButton = new Button("Połącz z serwerem"); // Utworzenie przycisku
        Text statusText = new Text(); // Utworzenie tekstu do wyświetlania statusu

        connectButton.setOnAction(e -> { // Ustawienie akcji dla przycisku
            try {
                connectToServer(); // Próba połączenia z serwerem
                statusText.setText("Połączono z serwerem"); // Zmiana tekstu statusu
                openMainMenu(); // Otwarcie głównego menu
            } catch (IOException ex) { // Obsługa wyjątków wejścia/wyjścia
                statusText.setText("Błąd połączenia: " + ex.getMessage()); // Wyświetlenie błędu
                ex.printStackTrace(); // Wydrukowanie śladu stosu
            }
        });

        root.getChildren().addAll(connectButton, statusText); // Dodanie przycisku i tekstu do kontenera

        primaryStage.setScene(new Scene(root, 300, 150)); // Ustawienie sceny dla okna
        primaryStage.setTitle("Klient"); // Ustawienie tytułu okna
        primaryStage.show(); // Wyświetlenie okna
    }

    private void connectToServer() throws IOException { // Metoda do nawiązania połączenia z serwerem
        socket = new Socket(serverAddress, serverPort); // Utworzenie gniazda sieciowego
        out = new PrintWriter(socket.getOutputStream(), true); // Inicjalizacja strumienia wyjściowego
        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Inicjalizacja strumienia wejściowego
    }

    private void openMainMenu() { // Metoda do otwarcia głównego menu
        View mainMenu = new View(primaryStage); // Utworzenie instancji klasy View z głównym menu
        mainMenu.showMenu(); // Wywołanie metody wyświetlającej menu
    }

    @Override
    public void stop() { // Metoda wywoływana przy zamykaniu aplikacji
        // Tutaj miejsce na zamknięcie zasobów sieciowych, np. socket.close();
    }

    public static void main(String[] args) { // Główna metoda uruchomieniowa
        launch(args); // Uruchomienie aplikacji JavaFX
    }
}
