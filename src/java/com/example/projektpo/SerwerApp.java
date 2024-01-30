package com.example.projektpo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SerwerApp extends Application {
    private Serwer serwer;
    private Text statusText;

    @Override
    public void start(Stage primaryStage) {
        serwer = new Serwer(8080, 10);
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        Button startButton = new Button("Uruchom serwer");
        Button stopButton = new Button("Zatrzymaj serwer");
        statusText = new Text("Status: Wyłączony");

        startButton.setOnAction(e -> startServer());
        stopButton.setOnAction(e -> stopServer());

        root.getChildren().addAll(startButton, stopButton, statusText);

        primaryStage.setScene(new Scene(root, 300, 150));
        primaryStage.setTitle("Serwer");
        primaryStage.show();
    }

    private void startServer() {
        new Thread(() -> {
            serwer.startServer();
            updateStatus();
        }).start();
        updateStatus();
    }

    private void stopServer() {
        serwer.stopServer();
        updateStatus();
    }

    private void updateStatus() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String status = serwer.isRunning() ? "Uruchomiony na adresie: " + ip.getHostAddress() + ", port: 12345" : "Wyłączony";
            statusText.setText("Status: " + status);
        } catch (UnknownHostException e) {
            statusText.setText("Błąd: Nie można uzyskać adresu IP");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
