package yu.heetae.reversi;

import java.io.Serializable;

/**
 * Created by yu on 9/10/15.
 */
public class Message implements Serializable{

    private int row;
    private int col;
    private int color;
    private int whiteScore;
    private int blackScore;
    private boolean[] directionsToFlip;
    private String message;
    private Square[][] board;


    private final int WHITE = 0;
    private final int BLACK = 1;
    private final int EMPTY = -1;

    public Message(int row, int col, boolean[] directionsToFlip, String message, Square[][] board, int color) {
        this.row = row;
        this.col = col;
        this.directionsToFlip = directionsToFlip;
        this.message = message;
        this.board = board;
        this.color = color;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean[] getDirectionsToFlip() {
        return directionsToFlip;
    }

    public String getMessage() {
        return message;
    }

    public Square[][] getBoard() {
        return board;
    }

    public void setBoard(Square[][] board) {
        this.board = board;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public void setWhiteScore(int whiteScore) {
        this.whiteScore = whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public void setBlackScore(int blackScore) {
        this.blackScore = blackScore;
    }
}
