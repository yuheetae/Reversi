package yu.heetae.reversi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by yu on 8/28/15.
 */
public class Board {

    private Square[][] board = new Square[8][8];
    private final int WHITE = 0;
    private final int BLACK = 1;
    private final int EMPTY = -1;
    private int playerColor;
    private int opponentColor;
    private int playerScore = 2;
    private int opponentScore = 2;


    public Board(int playerColor) {
        //Set play and opponent colors
        if(playerColor == BLACK) {
            this.playerColor = BLACK;
            opponentColor = WHITE;
        }
        else {
            this.playerColor = WHITE;
            opponentColor = BLACK;
        }

        resetBoard();
    }


    private void resetBoard() {

        //Reinitialize square objects
        for(Square[] squares : board) {
            for(Square square : squares) {
                square = new Square();
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
        playerScore = opponentScore = 2;
    }

    private boolean[] searchDirectionsToFlip(int row, int col) {

        //boolean array, indices indicating if pieces will flipped in certain directions
        //Directions in from index 0 to 7: N, E, S, W, NE, NW, SE, SW
        boolean[] directionsToFlip = new boolean[8];

        //array containing x increment values for each direction, e.g. to check north increment by -1 in x direction
        int[] xDirection = {-1, 0, 0, 1, 1, 0, 0, -1};
        //array containing y increment values for each direction
        int[] yDirection = {0, 1, 0, -1, 1, -1, 1, -1};


        //temp variables to reset row and col to original values
        int originalRow = row;
        int originalCol = col;

        for(int i=0; i<8; ++i) {

            //reset row/col to original values
            row = originalRow;
            col = originalCol;

            int xIncrement = xDirection[i];
            int yIncrement = yDirection[i];

            //if adjacent square does not have an opponent piece, no pieces to flip in direction
            if(board[row += xIncrement][col += yIncrement].getStatus() != opponentColor) directionsToFlip[i] = false;
                //else check if there are pieces to flip in direction
            else {
                directionsToFlip[i] = checkDirection(row, col, xIncrement, yIncrement);
                if(directionsToFlip[i] == true) board[originalRow][originalCol].setIsValidMove(true);
            }

        }

        return directionsToFlip;
    }

    private boolean checkDirection(int row, int col, int xIncrement, int yIncrement) {

        //Make sure squares being checked are in bounds
        for (; (row < 8 && row >= 0) || (col < 8 && col >= 0); ) {

            //increment row/col
            row += xIncrement;
            col += yIncrement;

            //Empty square, so not a valid move in this direction
            if (board[row][col].getStatus() == EMPTY) return false;
                //Player Color, so valid move
            else if (board[row][col].getStatus() == playerColor) return true;
        }
        return false;
    }
}
