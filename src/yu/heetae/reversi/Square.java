package yu.heetae.reversi;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.Serializable;

//Object Class for individual squares on reversi board
public class Square implements Serializable {

    private int status = -1;     //-1 = empty, 0 = white, 1 = black

    private final int WHITE = 0;
    private final int BLACK = 1;
    private final int EMPTY = -1;

    private boolean isValidMove = false;
    private boolean[] directionsToFlip = new boolean[8];//index 0 = N, 1 = E, 2 = S, 3 = W, 4 = NE, 5 = NW, 6 = SE, 7 = SW

    public Square() {

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isValidMove() {
        return isValidMove;
    }

    public void setIsValidMove(boolean isValidMove) {
        this.isValidMove = isValidMove;
    }

    public boolean[] getDirectionsToFlip() {
        return directionsToFlip;
    }

    public void setDirectionsToFlip(boolean[] possibleMoves) {
        this.directionsToFlip = possibleMoves;
    }


}
