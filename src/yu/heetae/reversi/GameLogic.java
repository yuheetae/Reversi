package yu.heetae.reversi;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by yu on 8/26/15.
 */
public class GameLogic {

    private Square[][] board = new Square[8][8];
    private final int WHITE = 0;
    private final int BLACK = 1;
    private final int EMPTY = -1;
    private int playerColor;
    private int opponentColor;

    private int whiteScore = 2;
    private int blackScore = 2;

    //index 0 = N, 1 = E, 2 = S, 3 = W, 4 = NE, 5 = NW, 6 = SE, 7 = SW

    //array containing x increment values for each direction, e.g. to check north increment by -1 in x direction
    private int[] xDirection = {0, 1, 0, -1, 1, -1, 1, -1};
    //array containing y increment values for each direction
    private int[] yDirection = {-1, 0, 1, 0, -1, -1, 1, 1};

    public GameLogic(int playerColor) {

        //Set player and opponent colors
        if(playerColor == BLACK) {
            this.playerColor = BLACK;
            opponentColor = WHITE;
        }
        else {
            this.playerColor = WHITE;
            opponentColor = BLACK;
        }

        //reset board to starting state
        resetBoard();
    }

    //Resets board to starting state
    public void resetBoard() {

        //Reinitialize square objects
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                board[i][j] = new Square();
            }
        }

        //Set board pieces to start game positions
        board[3][3].setStatus(WHITE);
        board[4][4].setStatus(WHITE);
        board[3][4].setStatus(BLACK);
        board[4][3].setStatus(BLACK);

        //Set valid moves for start game positions
        if(playerColor == WHITE) {
            boolean[] possibleMoves1 = {false, false, true, false, false, false, false, false};
            boolean[] possibleMoves2 = {false, false, false, true, false, false, false, false};
            boolean[] possibleMoves3 = {false, true, false, false, false, false, false, false};
            boolean[] possibleMoves4 = {true, false, false, false, false, false, false, false};


            board[2][4].setIsValidMove(true);
            board[3][5].setIsValidMove(true);
            board[4][2].setIsValidMove(true);
            board[5][3].setIsValidMove(true);

            board[2][4].setDirectionsToFlip(possibleMoves1);
            board[3][5].setDirectionsToFlip(possibleMoves2);
            board[4][2].setDirectionsToFlip(possibleMoves3);
            board[5][3].setDirectionsToFlip(possibleMoves4);
        }
        else {
            boolean[] possibleMoves1 = {false, false, true, false, false, false, false, false};
            boolean[] possibleMoves2 = {false, true, false, false, false, false, false, false};
            boolean[] possibleMoves3 = {false, false, false, true, false, false, false, false};
            boolean[] possibleMoves4 = {true, false, false, false, false, false, false, false};

            board[2][3].setIsValidMove(true);
            board[3][2].setIsValidMove(true);
            board[4][5].setIsValidMove(true);
            board[5][4].setIsValidMove(true);

            board[2][3].setDirectionsToFlip(possibleMoves1);
            board[3][2].setDirectionsToFlip(possibleMoves2);
            board[4][5].setDirectionsToFlip(possibleMoves3);
            board[5][4].setDirectionsToFlip(possibleMoves4);
        }

        //Reset game score
        whiteScore = blackScore = 2;
    }

    //Iterate through board to determine which boards have valid moves, and returns number of valid moves
    public int checkForMoves() {
        int numberOfValidMoves = 0;

        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                //if square is not occupied by a piece check if valid move
                if(board[i][j].getStatus() == EMPTY) {
                    //set square's directionsToFlip array
                    searchDirectionsToFlip(i, j);
                    //if the square is a valid move increment numberOfValidMoves
                    if(board[i][j].isValidMove()) {
                        numberOfValidMoves++;
                    }
                }
            }
        }   //end for loop

        return numberOfValidMoves;
    }

    //Determines in which directions pieces will be flipped for a square, sets square's directionToFlip array
    private void searchDirectionsToFlip(int row, int col) {

        //boolean array, indices indicating if pieces will flipped in certain directions
        //Directions in from index 0 to 7: N, E, S, W, NE, NW, SE, SW
        boolean[] directionsToFlip = new boolean[8];

        boolean isValid = false;

        //temp variables to reset row and col to original values
        int originalRow = row;
        int originalCol = col;

        for(int i=0; i<8; i++) {

            //reset row/col to original values
            row = originalRow;
            col = originalCol;

            //Set increments depending on direction
            int xIncrement = xDirection[i];
            int yIncrement = yDirection[i];

            //System.out.println("Row: " + row + "    Col: " + col + "   i: " + i);

            directionsToFlip[i] = checkDirection(row, col, xIncrement, yIncrement);

            if(directionsToFlip[i] == true) isValid |= true;

        }

        board[originalRow][originalCol].setIsValidMove(isValid);
        board[row][col].setDirectionsToFlip(directionsToFlip);
    }

    //Check if a square has a valid move in a direction
    private boolean checkDirection(int row, int col, int xIncrement, int yIncrement) {

        //increment row/col
        row += yIncrement;
        col += xIncrement;

        if(row > 7 || row < 0 || col > 7 || col < 0) return false;

        //if first adjacent square has player piece or empty, no pieces to flip in direction, return false
        if(board[row][col].getStatus() == playerColor || board[row][col].getStatus() == EMPTY) return false;    //edit error

        //increment row/col
        row += yIncrement;
        col += xIncrement;

        //Make sure squares being checked are in bounds
        while(row < 8 && row >= 0 && col < 8 && col >= 0) {

            //Empty square, so not a valid move in this direction
            if (board[row][col].getStatus() == EMPTY) return false;
            //Player Color, so valid move
            else if (board[row][col].getStatus() == playerColor) return true;

            //increment row/col
            row += yIncrement;
            col += xIncrement;
        }
        return false;
    }

    //Flip disks
    public void flipDisks(boolean[] directionsToFlip, int row, int col, int color) {

        int scoreIncrement = 0;

        //Get directions to flip
        if(directionsToFlip ==null) {
            directionsToFlip = board[row][col].getDirectionsToFlip();
        }

        //temp variables to reset row and col to original values
        int originalRow = row;
        int originalCol = col;

        //update square's status and make it a non valid move
        board[row][col].setStatus(color);
        board[row][col].setIsValidMove(false);

        for (int i = 0; i < 8; i++) {

            //reset row/col to original values
            row = originalRow;
            col = originalCol;

            //If there are disks to flip in a direction flip them
            if (directionsToFlip[i]) {
                //Set increments depending on direction
                int xIncrement = xDirection[i];
                int yIncrement = yDirection[i];

                //while the square doesn't have a disk of players color keep flipping disks
                while(board[row += yIncrement][col += xIncrement].getStatus() != color) {
                    board[row][col].setStatus(color);
                    board[row][col].setIsValidMove(false);
                    scoreIncrement++;
                }

            }
        }

        if(color == WHITE) {
            whiteScore += scoreIncrement + 1;
            blackScore -= scoreIncrement;
        }
        else {
            blackScore += scoreIncrement + 1;
            whiteScore -= scoreIncrement;
        }

    }

    public Square getSquare(int i, int j) {
        return board[i][j];
    }

    public boolean[] getDirectionsToFlip(int row, int col) {
        return board[row][col].getDirectionsToFlip();
    }


    public int getPlayerColor() {
        return playerColor;
    }

    public int getOpponentColor() {
        return opponentColor;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public int getWhiteScore() {
        return whiteScore;
    }


}

















