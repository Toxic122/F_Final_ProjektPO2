package com.example.projektpo;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.projektpo.Obslugaklienta;
import com.example.projektpo.Wizyta;


public class View {
    private Stage stage;

    public View(Stage stage) {
        this.stage = stage;
    }

    public void showMenu() {
        VBox layout = new VBox(10);
        layout.setSpacing(10);


        // Przyciski dla różnych funkcji
        // Przyciski dla różnych funkcji
        Button manageClientsButton = new Button("Zarządzaj klientami");
        manageClientsButton.setOnAction(e -> {
            Obslugaklienta obslugaKlienta = new Obslugaklienta(stage, this); // Przekazanie 'this'
            obslugaKlienta.showClientManagement();
        });


        Button manageAppointmentsButton = new Button("Zarządzaj wizytami");
        manageAppointmentsButton.setOnAction(e -> {
            Wizyta wizyta = new Wizyta(stage, this);
            wizyta.showManageAppointments();
        });




        Button manageQueueButton = new Button("Zarządzaj kolejką");
        manageQueueButton.setOnAction(e -> {
            Zarzadzaniekolejka zarzadzanieKolejka = new Zarzadzaniekolejka(stage, this);

            zarzadzanieKolejka.showQueueManagementUI();
        });


        Button manageDocumentsButton = new Button("Autorzy");
        manageDocumentsButton.setOnAction(e -> {
            Autorzy autorzy = new Autorzy(stage);
            autorzy.showAuthors();
        });





        // Dodawanie przycisków do layoutu
        layout.getChildren().addAll(manageClientsButton, manageAppointmentsButton, manageQueueButton, manageDocumentsButton);

        // Ustawienie i wyświetlenie sceny
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Główne Menu");
        stage.show();
    }
}
