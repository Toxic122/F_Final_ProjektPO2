package com.example.projektpo;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

// ...

public class Autorzy {

    private Stage stage;

    public Autorzy(Stage stage) {
        this.stage = stage;
    }

    public void showAuthors() {
        VBox layout = new VBox(10);
        layout.setSpacing(10);

        Label titleLabel = new Label("Autorzy i Informacje");
        TextArea authorsText = new TextArea("Nazwa Projektu: System obslugi klienta\nProjekt wykonał: Jakub Paluch oraz Mirosław Dudek\n Grupa: 2IZ11A ");
        authorsText.setEditable(false); //pole tekstowe było tylko do odczytu



        Button downloadReportButton = new Button("Wyświetl sprawozdanie");
        downloadReportButton.setOnAction(e -> {
            File pdfFile = new File("Sprawozdanie.pdf");
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(pdfFile);
                    } catch (IOException ex) {
                        // Obsługa błędu
                        System.err.println("Nie można otworzyć pliku: " + ex.getMessage());
                    }
                } else {
                    // Informacja dla użytkownika, jeśli Desktop nie jest wspierany
                    System.err.println("Funkcja Desktop nie jest wspierana");
                }
            } else {
                // Informacja dla użytkownika, jeśli plik nie istnieje
                System.err.println("Plik nie istnieje");
            }
        });




        Button backButton = new Button("Powrót");
        backButton.setOnAction(e -> {
            // Tutaj umieść kod obsługujący powrót do głównego menu
            View mainMenuView = new View(stage); // Utwórz instancję widoku głównego menu (zmień na właściwą klasę, jeśli potrzebujesz innej)
            mainMenuView.showMenu(); // Wywołaj metodę wyświetlającą główne menu
        });

        layout.getChildren().addAll(titleLabel, authorsText, downloadReportButton, backButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}
