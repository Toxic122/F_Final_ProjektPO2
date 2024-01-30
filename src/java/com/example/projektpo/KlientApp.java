package com.example.projektpo;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class KlientApp extends Application {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String serverAddress = "192.168.56.1"; // Adres IP serwera
    private final int serverPort = 12345; // Port serwera
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        Button connectButton = new Button("Połącz z serwerem");
        Text statusText = new Text();

        connectButton.setOnAction(e -> {
            try {
                connectToServer();
                statusText.setText("Połączono z serwerem");
                openMainMenu();
            } catch (IOException ex) {
                statusText.setText("Błąd połączenia: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(connectButton, statusText);

        primaryStage.setScene(new Scene(root, 300, 150));
        primaryStage.setTitle("Klient");
        primaryStage.show();
    }

    private void connectToServer() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void openMainMenu() {
        View mainMenu = new View(primaryStage);
        mainMenu.showMenu();
    }

    @Override
    public void stop() {
        // Zamknięcie zasobów sieciowych...
    }

    public static void main(String[] args) {
        launch(args);
    }
}
