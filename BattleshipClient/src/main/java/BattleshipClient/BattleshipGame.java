package BattleshipClient;

import java.util.Scanner;

public class BattleshipGame {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Grid player1Grid = new Grid(10, 10, 5); // Example setup
        Grid player2Grid = new Grid(10, 10, 5);

        // Assume ships are already placed for simplification
        // Normally you would have a method to place ships here
        player1Grid.setShip("A1","A3");
        player1Grid.setShip("B1","B4");
        player1Grid.setShip("C1","C5");
        player1Grid.setShip("C6","F6");
        player1Grid.setShip("A7","F7");
        player1Grid.printBoard();

        player2Grid.setShip("A1","A3");
        player2Grid.setShip("B1","B4");
        player2Grid.setShip("C1","C5");
        player2Grid.setShip("C6","F6");
        player2Grid.setShip("A7","F7");
        player2Grid.printBoard();

        boolean gameOver = false;
        while (!gameOver) {
            System.out.println("Player 1, enter coordinates to hit (e.g., D3):");
            String p1Hit = scanner.nextLine();
            Ship hitResult = player2Grid.doHit(p1Hit);
            if (hitResult != null) {
                System.out.println("Player 1 hits Player 2 at " + p1Hit);
                player2Grid.printBoard();
                if (hitResult.health < 0) {
                    System.out.println("Player 1 has sunk Player 2's ship!");
                }
            } else {
                System.out.println("Player 1 misses at " + p1Hit);
            }

            // Check if all ships are sunk
            if (player2Grid.shipsRemaining() == 0) {
                System.out.println("Player 1 wins!");
                gameOver = true;
                continue;
            }

            System.out.println("Player 2, enter coordinates to hit (e.g., F6):");
            String p2Hit = scanner.nextLine();
            hitResult = player1Grid.doHit(p2Hit);
            if (hitResult != null) {
                player1Grid.printBoard();
                System.out.println("Player 2 hits Player 1 at " + p2Hit);
                if (hitResult.health == 0) {
                    System.out.println("Player 2 has sunk Player 1's ship!");
                }
            } else {
                System.out.println("Player 2 misses at " + p2Hit);
            }

            // Check if all ships are sunk
            if (player1Grid.shipsRemaining() == 0) {
                System.out.println("Player 2 wins!");
                gameOver = true;
            }
        }

        scanner.close();
    }
}
