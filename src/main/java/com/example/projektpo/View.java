package com.example.projektpo; // Definiuje pakiet dla klasy, co pomaga w organizacji kodu w projekcie.

// Importowanie potrzebnych klas z JavaFX do tworzenia interfejsu użytkownika.
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Importowanie klas reprezentujących różne widoki w aplikacji.
import com.example.projektpo.Obslugaklienta;
import com.example.projektpo.Wizyta;

public class View {
    private Stage stage; // Prywatne pole przechowujące referencję do głównego okna aplikacji.

    // Konstruktor klasy, przyjmuje Stage jako argument.
    public View(Stage stage) {
        this.stage = stage; // Przypisuje przekazany Stage do prywatnego pola klasy.
    }

    // Metoda do wyświetlenia głównego menu aplikacji.
    public void showMenu() {
        VBox layout = new VBox(10); // Tworzy kontener VBox do organizacji elementów w pionie z odstępem 10 pikseli.
        layout.setSpacing(10); // Ustawia odstęp między elementami w kontenerze.

        // Tworzenie przycisków do zarządzania różnymi aspektami aplikacji.
        Button manageClientsButton = new Button("Zarządzaj klientami");
        // Ustawienie akcji dla przycisku - otwarcie widoku zarządzania klientami.
        manageClientsButton.setOnAction(e -> {
            Obslugaklienta obslugaKlienta = new Obslugaklienta(stage, this); // Tworzy instancję obsługi klienta.
            obslugaKlienta.showClientManagement(); // Wywołuje metodę wyświetlającą interfejs zarządzania klientami.
        });

        Button manageAppointmentsButton = new Button("Zarządzaj wizytami");
        // Ustawienie akcji dla przycisku - otwarcie widoku zarządzania wizytami.
        manageAppointmentsButton.setOnAction(e -> {
            Wizyta wizyta = new Wizyta(stage, this); // Tworzy instancję obsługi wizyt.
            wizyta.showManageAppointments(); // Wywołuje metodę wyświetlającą interfejs zarządzania wizytami.
        });

        Button manageQueueButton = new Button("Zarządzaj kolejką");
        // Ustawienie akcji dla przycisku - otwarcie widoku zarządzania kolejką.
        manageQueueButton.setOnAction(e -> {
            Zarzadzaniekolejka zarzadzanieKolejka = new Zarzadzaniekolejka(stage, this); // Tworzy instancję obsługi kolejki.
            zarzadzanieKolejka.showQueueManagementUI(); // Wywołuje metodę wyświetlającą interfejs zarządzania kolejką.
        });

        Button manageDocumentsButton = new Button("Autorzy");
        // Ustawienie akcji dla przycisku - otwarcie widoku informacji o autorach.
        manageDocumentsButton.setOnAction(e -> {
            Autorzy autorzy = new Autorzy(stage); // Tworzy instancję widoku autorów.
            autorzy.showAuthors(); // Wywołuje metodę wyświetlającą informacje o autorach.
        });

        // Dodanie przycisków do kontenera layout.
        layout.getChildren().addAll(manageClientsButton, manageAppointmentsButton, manageQueueButton, manageDocumentsButton);

        // Ustawienie sceny z utworzonym layoutem i wyświetlenie jej w głównym oknie aplikacji.
        Scene scene = new Scene(layout, 400, 300); // Tworzy scenę z określonymi wymiarami.
        stage.setScene(scene); // Ustawia scenę dla głównego okna.
        stage.setTitle("Główne Menu"); // Ustawia tytuł głównego okna.
        stage.show(); // Wyświetla główne okno aplikacji.
    }
}
