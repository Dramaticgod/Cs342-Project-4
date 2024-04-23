package BattleshipClient;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Scanner scanner;

    public Client(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port); // Connect to the server at the specified address and port
            out = new ObjectOutputStream(socket.getOutputStream()); // Stream for sending commands to the server
            in = new ObjectInputStream(socket.getInputStream()); // Stream for receiving responses from the server
            scanner = new Scanner(System.in); // Scanner for user input
        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            System.exit(1); // Exit if we cannot establish a connection
        }
    }

    public void start() {
        try {
            boolean setupComplete = false;
            while (true) {
                String serverMessage = (String) in.readObject();
                System.out.println("Server says: " + serverMessage);

                if (serverMessage.toLowerCase().contains("enter coordinates for ship") && !setupComplete) {
                    System.out.print("Enter your input for ship placement (StartCoord EndCoord): ");
                    String userInput = scanner.nextLine();
                    out.writeObject(userInput);
                } else if (serverMessage.toLowerCase().contains("enter your move") || serverMessage.toLowerCase().contains("your turn")) {
                    System.out.print("Enter your move (coordinate to attack): ");
                    String move = scanner.nextLine();
                    out.writeObject(move);
                } else if (serverMessage.toLowerCase().contains("ship placement complete")) {
                    setupComplete = true;  // Flag to indicate ship placement is complete
                } else if (serverMessage.toLowerCase().contains("wins")) {
                    System.out.println("Game Over: " + serverMessage);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error during communication: " + e.getMessage());
        } finally {
            close();
        }
    }


    private void close() { // Helper method to close resources
        try {
            scanner.close(); // Close the scanner
            in.close(); // Close the input stream
            out.close(); // Close the output stream
            socket.close(); // Close the socket connection
        } catch (Exception e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 5555);
        client.start(); // Start the client
    }
}
