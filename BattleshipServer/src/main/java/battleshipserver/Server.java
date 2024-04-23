package  battleshipserver;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Server {
    private ServerSocket serverSocket;
    private Socket player1Socket;
    private Socket player2Socket;
    private Grid player1Grid;
    private Grid player2Grid;
    private boolean player1Turn = true;

    public Server(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started. Waiting for players...");

        player1Socket = serverSocket.accept();
        ObjectOutputStream out1 = new ObjectOutputStream(player1Socket.getOutputStream());
        ObjectInputStream in1 = new ObjectInputStream(player1Socket.getInputStream());
        System.out.println("Player 1 connected.");

        player2Socket = serverSocket.accept();
        ObjectOutputStream out2 = new ObjectOutputStream(player2Socket.getOutputStream());
        ObjectInputStream in2 = new ObjectInputStream(player2Socket.getInputStream());
        System.out.println("Player 2 connected.");

        player1Grid = new Grid(10, 10, 5);
        player2Grid = new Grid(10, 10, 5);

        setupShips(in1, out1, player1Grid, "Player 1");
        setupShips(in2, out2, player2Grid, "Player 2");

        startGame(out1,in1,out2,in2);
    }

    private void setupShips(ObjectInputStream in, ObjectOutputStream out, Grid grid, String playerName) throws Exception {
        out.writeObject(playerName + ", enter coord for 5 ships. Format: StartCoord EndCoord (e.g., A1 A5)");

        int shipsToPlace = 5;
        for (int i = 0; i < shipsToPlace; i++) {
            out.writeObject("Enter coordinates for ship " + (i + 1) + " (e.g., A1 A5):");
            String coords = (String) in.readObject();
            // Split the coordinates string into start and end parts
            String[] parts = coords.split(" ");
            if (parts.length == 2) {
                Ship ship = grid.setShip(parts[0], parts[1]);
                if (ship != null) {
                    out.writeObject("Ship " + (i + 1) + " placed at " + parts[0] + " to " + parts[1]);
                } else {
                    out.writeObject("Invalid coordinates or overlap, please re-enter.");
                    i--; // Decrement to retry placing this ship
                }
            } else {
                out.writeObject("Invalid input format, please use StartCoord EndCoord.");
                i--; // Decrement to retry placing this ship
            }
        }
    }

    private void startGame(ObjectOutputStream out1,ObjectInputStream in1,ObjectOutputStream out2, ObjectInputStream in2) {
        try  {
            boolean gameOver = false;
            while (!gameOver) {
                if (player1Turn) {
                    out1.writeObject("Your turn: Enter your move.");
                    String move = (String) in1.readObject();
                    Ship hitShip = player2Grid.doHit(move);
                    String result = hitShip != null ? "Hit at " + move : "Miss at " + move;
                    player2Grid.printBoard();
                    out1.writeObject("Result of your move: " + result);
                    out2.writeObject("Player 1 moved: " + move + " - Result: " + result);
                    if(hitShip ==  null) {
                        player1Turn = false;
                    }
                } else {
                    out2.writeObject("Your turn: Enter your move.");
                    String move = (String) in2.readObject();
                    Ship hitShip = player1Grid.doHit(move);
                    String result = hitShip != null ? "Hit at " + move : "Miss at " + move;
                    player1Grid.printBoard();
                    out2.writeObject("Result of your move: " + result);
                    out1.writeObject("Player 2 moved: " + move + " - Result: " + result);
                    if(hitShip ==  null) {
                        player1Turn = true;
                    }
                }

                // Check for game over condition here
                if (player1Grid.shipsRemaining() == 0 || player2Grid.shipsRemaining() == 0) {
                    gameOver = true;
                    String winner = player1Grid.shipsRemaining() == 0 ? "Player 2" : "Player 1";
                    out1.writeObject(winner + " wins!");
                    out2.writeObject(winner + " wins!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new Server(5555);
        } catch (Exception e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }
}
