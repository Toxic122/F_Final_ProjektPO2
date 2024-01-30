package com.example.projektpo; // Deklaracja pakietu, w którym znajduje się klasa

import javafx.scene.Scene; // Import klasy Scene z biblioteki JavaFX
import javafx.scene.control.Button; // Import klasy Button z biblioteki JavaFX
import javafx.scene.control.Label; // Import klasy Label z biblioteki JavaFX
import javafx.scene.control.TextArea; // Import klasy TextArea z biblioteki JavaFX
import javafx.scene.layout.VBox; // Import klasy VBox z biblioteki JavaFX
import javafx.stage.Stage; // Import klasy Stage z biblioteki JavaFX
import java.awt.Desktop; // Import klasy Desktop z AWT do interakcji z aplikacjami desktopowymi
import java.io.File; // Import klasy File do obsługi plików
import java.io.IOException; // Import wyjątku IOException do obsługi błędów I/O

public class Autorzy { // Deklaracja klasy Autorzy

    private Stage stage; // Deklaracja prywatnego pola stage typu Stage

    public Autorzy(Stage stage) { // Konstruktor klasy przyjmujący obiekt Stage
        this.stage = stage; // Przypisanie przekazanego obiektu Stage do pola klasy
    }

    public void showAuthors() { // Metoda wyświetlająca informacje o autorach
        VBox layout = new VBox(10); // Utworzenie kontenera VBox z odstępem między elementami równym 10
        layout.setSpacing(10); // Ustawienie odstępu między elementami na 10

        Label titleLabel = new Label("Autorzy i Informacje"); // Utworzenie etykiety z tekstem
        TextArea authorsText = new TextArea("Nazwa Projektu: System obslugi klienta\nProjekt wykonał: Jakub Paluch oraz Mirosław Dudek\n Grupa: 2IZ11A "); // Utworzenie obszaru tekstowego z informacjami o autorach
        authorsText.setEditable(false); // Ustawienie obszaru tekstowego na nieedytowalny

        Button downloadReportButton = new Button("Wyświetl sprawozdanie"); // Utworzenie przycisku
        downloadReportButton.setOnAction(e -> { // Ustawienie akcji dla przycisku
            File pdfFile = new File("Sprawozdanie.pdf"); // Utworzenie obiektu File dla pliku PDF
            if (pdfFile.exists()) { // Sprawdzenie, czy plik istnieje
                if (Desktop.isDesktopSupported()) { // Sprawdzenie, czy Desktop jest wspierany
                    try {
                        Desktop.getDesktop().open(pdfFile); // Próba otwarcia pliku PDF
                    } catch (IOException ex) { // Obsługa wyjątku IOException
                        System.err.println("Nie można otworzyć pliku: " + ex.getMessage()); // Wyświetlenie komunikatu o błędzie
                    }
                } else {
                    System.err.println("Funkcja Desktop nie jest wspierana"); // Informacja, że Desktop nie jest wspierany
                }
            } else {
                System.err.println("Plik nie istnieje"); // Informacja, że plik nie istnieje
            }
        });

        Button backButton = new Button("Powrót"); // Utworzenie przycisku "Powrót"
        backButton.setOnAction(e -> { // Ustawienie akcji dla przycisku "Powrót"
            View mainMenuView = new View(stage); // Utworzenie nowej instancji View dla głównego menu
            mainMenuView.showMenu(); // Wywołanie metody wyświetlającej główne menu
        });

        layout.getChildren().addAll(titleLabel, authorsText, downloadReportButton, backButton); // Dodanie elementów do layoutu

        Scene scene = new Scene(layout, 400, 300); // Utworzenie sceny z określonym layoutem i rozmiarami
        stage.setScene(scene); // Ustawienie sceny dla obiektu Stage
        stage.show(); // Wyświetlenie Stage
    }
}
