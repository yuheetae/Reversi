package yu.heetae.reversi;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

//Object Class for individual squares on reversi board
public class Square extends JButton{

    private int status = -1;     //-1 = empty, 0 = white, 1 = black

    private final int WHITE = 0;
    private final int BLACK = 1;
    private final int EMPTY = -1;

    private ImageIcon whitePiece;
    private ImageIcon blackPiece;

    private boolean isValidMove;
    private boolean[] directionsToFlip = new boolean[8];//index 0 = N, 1 = E, 2 = S, 3 = W, 4 = NE, 5 = NW, 6 = SE, 7 = SW

    private Color boardGreen = new Color(57, 126, 88);
    private Color highlightYellow = new Color(255,255,102);


    public Square() {
        //this.status = status;
        setBackground(boardGreen);
        setOpaque(true);
        addMouseListener(new mouseListener());

        //Initialize Image variables for white/black pieces
        Image blackImg = null;
        Image whiteImg = null;

        try {
            //read in black/white pieces from file
            blackImg = ImageIO.read(UserInterface.class.getResource("/res/blackpiece.png"));
            whiteImg = ImageIO.read(UserInterface.class.getResource("/res/whitepiece.png"));
        } catch (IOException e) {

        }

        //Set black/white piece ImageIcons
        blackPiece = new ImageIcon(blackImg.getScaledInstance(43, 43, Image.SCALE_SMOOTH));
        whitePiece = new ImageIcon(whiteImg.getScaledInstance(43, 43, Image.SCALE_SMOOTH));

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

    public class mouseListener implements MouseListener {
        public void mouseEntered(MouseEvent e) {
            if(isValidMove) setBackground(highlightYellow);
        }

        public void mouseExited(MouseEvent e) {
            if(getBackground() == highlightYellow) setBackground(boardGreen);
        }

        public void mouseClicked(MouseEvent e) {

        }

        public void mousePressed(MouseEvent e) {

        }

        public void mouseReleased(MouseEvent e) {

        }
    }
}
