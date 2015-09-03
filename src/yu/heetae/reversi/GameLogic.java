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
    private int playerScore = 2;
    private int opponentScore = 2;

    private ImageIcon whitePiece;
    private ImageIcon blackPiece;

    //Borders
    private Border gridBorder = new LineBorder(Color.WHITE, 1);

    public GameLogic(int playerColor) {

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


    private void resetBoard() {

        //Reinitialize square objects
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                board[i][j] = new Square();
                board[i][j].addActionListener(new listener());
                board[i][j].putClientProperty("column", i);
                board[i][j].putClientProperty("row", j);
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


    //For an empty square determine in which directions disks will be flipped if any at all
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

            //Set increments depending on direction
            int xIncrement = xDirection[i];
            int yIncrement = yDirection[i];

            //if adjacent square does not have an opponent piece, no pieces to flip in direction
            if(board[row += xIncrement][col += yIncrement].getStatus() != opponentColor) {
                directionsToFlip[i] = false;
            }
            //else check if there are pieces to flip in direction
            else {
                directionsToFlip[i] = checkDirection(row, col, xIncrement, yIncrement);
                //if checkDirection returns true set square as a valid move
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

    public class listener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Square square = (Square) e.getSource();
            square.setIcon(blackPiece);
        }
    }

    public class boardSquareListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            //Get the square being clicked
            Square square = (Square) e.getSource();

            //If the square is not a valid move return
            if (!square.isValidMove()) return;

            //get square's index from client property
            int row = (int) square.getClientProperty("row");
            int col = (int) square.getClientProperty("column");

            flipDisks(row, col, playerColor);

        }
    }   //end of boardSquareListener

    public void flipDisks(int row, int col, int diskColor) {

        Square square = board[row][col];

        //Determine color to flip disks to
        ImageIcon disk;
        if(diskColor == WHITE) disk = whitePiece;
        else disk = blackPiece;

        //Get directions to flip
        boolean[] directionsToFlip = board[row][col].getDirectionsToFlip();

        //array containing x increment values for each direction, e.g. to check north increment by -1 in x direction
        int[] xDirection = {-1, 0, 0, 1, 1, 0, 0, -1};
        //array containing y increment values for each direction
        int[] yDirection = {0, 1, 0, -1, 1, -1, 1, -1};


        //temp variables to reset row and col to original values
        int originalRow = row;
        int originalCol = col;

        //Set square's icon and make it a non valid move
        square.setIcon(disk);
        square.setIsValidMove(false);

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
                while (board[row += xIncrement][col += yIncrement].getStatus() != playerColor) {
                    board[row][col].setIcon(disk);
                    board[row][col].setIsValidMove(false);
                }
            }
        }
    }

    public void startTurn(int row, int col, int diskColor) {


        for(int i=0; i<8; row++) {
            for(int j=0; j<8; col++) {
                if(board[i][j].getStatus() == EMPTY) {
                    searchDirectionsToFlip(row, col);
                }
            }
        }   //end for loop
    }

    public void endTurn() {

    }

    public JPanel buildBoard() {
        //Panel for game board
        JPanel boardPanel = new JPanel(new GridLayout(8,8,0,0));
        boardPanel.setPreferredSize(new Dimension(400,400));
        boardPanel.setBorder(gridBorder);

        //Initialize/Setup JButton 8x8 grid that will be the game board
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                board[i][j] = new Square();
                //board[i][j].setBackground(boardGreen);
                //board[i][j].setOpaque(true);

                //Set Borders For Game Board
                //Top row
                if (i==0) {
                    if (j==0) {
                        //Top left corner
                        board[i][j].setBorder(BorderFactory.createMatteBorder(4, 4, 2, 2, Color.BLACK));
                    }
                    else if(j==7) {
                        //Top right corner
                        board[i][j].setBorder(BorderFactory.createMatteBorder(4, 0, 2, 4, Color.BLACK));
                    }
                    else {
                        // Top edge
                        board[i][j].setBorder(BorderFactory.createMatteBorder(4, 0, 2, 2, Color.BLACK));
                    }
                }
                //Bottom row
                else if(i==7) {
                    if(j==0) {
                        //Bottom left corner
                        board[i][j].setBorder(BorderFactory.createMatteBorder(0, 4, 4, 2, Color.BLACK));
                    }
                    else if(j==7) {
                        //Bottom right corner
                        board[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, 4, 4, Color.BLACK));
                    }
                    else {
                        board[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, 4, 2, Color.BLACK));
                    }
                }
                else {
                    if (j==0) {
                        //Left-hand edge
                        board[i][j].setBorder(BorderFactory.createMatteBorder(0, 4, 2, 2, Color.BLACK));
                    }
                    else if(j==7) {
                        //Right-hand edge
                        board[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 4, Color.BLACK));
                    }
                    else {
                        //Non-edge buttons
                        board[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
                    }
                }

                //Add JButtons to board panel
                boardPanel.add(board[i][j]);
            }
        }
        return boardPanel;
    }


}

















