package com.example.projektpo; // Definiuje przestrzeń nazw dla klasy, co pomaga w organizacji kodu w projekcie.

// Import potrzebnych klas z biblioteki standardowej Javy.
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Serwer {
    private final int port; // Zmienna do przechowywania numeru portu, na którym serwer będzie nasłuchiwał.
    private ServerSocket serverSocket; // Socket serwera służący do nasłuchiwania przychodzących połączeń.
    private ExecutorService pool; // Pula wątków do obsługi wielu klientów jednocześnie.
    private volatile boolean isRunning; // Zmienna określająca, czy serwer jest uruchomiony. Słowo kluczowe 'volatile' zapewnia, że zmiana tej zmiennej będzie od razu widoczna w innych wątkach.

    // Metoda zwracająca stan serwera (uruchomiony/zatrzymany).
    public boolean isRunning() {
        return this.isRunning;
    }

    // Konstruktor klasy Serwer, przyjmujący numer portu i rozmiar puli wątków.
    public Serwer(int port, int poolSize) {
        this.port = 12345; // Przypisanie domyślnego numeru portu (zamiast użyć przekazanego argumentu 'port').
        pool = Executors.newFixedThreadPool(poolSize); // Inicjalizacja puli wątków o określonym rozmiarze.
    }

    // Metoda uruchamiająca serwer.
    public void startServer() {
        try {
            serverSocket = new ServerSocket(port); // Tworzenie gniazda serwera na określonym porcie.
            isRunning = true; // Ustawienie flagi uruchomienia serwera na true.
            while (isRunning) { // Pętla działa dopóki serwer jest uruchomiony.
                try {
                    Socket clientSocket = serverSocket.accept(); // Oczekiwanie na przychodzące połączenie i akceptacja.
                    pool.execute(new ClientHandler(clientSocket)); // Przekazanie obsługi klienta do oddzielnego wątku w puli.
                } catch (SocketException e) {
                    System.out.println("Serwer został zatrzymany."); // Informacja o zatrzymaniu serwera w przypadku wyjątku.
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Wypisanie stosu wywołań w przypadku błędu wejścia/wyjścia.
        }
    }

    // Metoda zatrzymująca serwer.
    public void stopServer() {
        isRunning = false; // Ustawienie flagi uruchomienia serwera na false.
        try {
            if (serverSocket != null) {
                serverSocket.close(); // Zamknięcie gniazda serwera, jeśli jest otwarte.
            }
        } catch (IOException e) {
            e.printStackTrace(); // Wypisanie stosu wywołań w przypadku błędu wejścia/wyjścia.
        }
        pool.shutdown(); // Zamknięcie puli wątków i zatrzymanie wszystkich działających zadań.
    }

    // Wewnętrzna klasa obsługująca połączenie z klientem.
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket; // Socket połączenia z klientem.

        // Konstruktor przyjmujący socket klienta.
        public ClientHandler(Socket socket) {
            this.clientSocket = socket; // Przypisanie socketu klienta do zmiennej klasy.
        }

        @Override
        public void run() {
            // Tutaj obsługa połączenia z klientem.
            // Metoda, która będzie wykonana w osobnym wątku dla każdego klienta.
            // Można tu np. odczytać dane wysłane przez klienta, przetworzyć je i odpowiedzieć.
        }
    }
}
