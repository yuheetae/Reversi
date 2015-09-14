package yu.heetae.reversi;

import java.io.Serializable;

/**
 * Created by yu on 9/10/15.
 */
public class Message implements Serializable{

    private int row;
    private int col;
    private boolean[] directionsToFlip;
    private String message;

    public Message(int row, int col, boolean[] directionsToFlip, String message) {
        this.row = row;
        this.col = col;
        this.directionsToFlip = directionsToFlip;
        this.message = message;
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
}
