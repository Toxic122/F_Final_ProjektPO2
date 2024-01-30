package com.example.projektpo;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Serwer {
    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private volatile boolean isRunning;
    public boolean isRunning() {
        return this.isRunning;
    }
    public Serwer(int port, int poolSize) {
        this.port = 12345;
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    pool.execute(new ClientHandler(clientSocket));
                } catch (SocketException e) {
                    System.out.println("Serwer został zatrzymany.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.shutdown();
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            // Tutaj obsługa połączenia z klientem
        }
    }
}
