package server;

import java.io.*;
import java.net.*;

public class ServerMain {

    public static void main(String[] args) {
        int port = 5555;
        System.out.println("Server started on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);

                if (message.startsWith("LOGIN_REQUEST")) {
                    handleLogin(message, out);
                } else {
                    out.println("UNKNOWN_COMMAND");
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        }
    }

    private static void handleLogin(String message, PrintWriter out) {
        String[] parts = message.split(":"); //Checks validity of remote; safe guard

        if (parts.length < 3) {
            out.println("LOGIN_FAIL:MALFORMED_REQUEST");
            return;
        }

        String username = parts[1];
        String password = parts[2];

        // Temp credentials — swap for firebase DB later (user admin, password admin)
        if ("admin".equals(username) && "admin".equals(password)) {
            System.out.println("Login success for user: " + username);
            out.println("LOGIN_SUCCESS");
        } else {
            System.out.println("Login failed for user: " + username);
            out.println("LOGIN_FAIL:INVALID_CREDENTIALS");
        }
    }
}