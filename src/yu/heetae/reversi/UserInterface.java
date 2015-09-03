package yu.heetae.reversi;

import net.miginfocom.swing.MigLayout;

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
public class UserInterface {

    private Square[][] board = new Square[8][8];
    private final int WHITE = 0;
    private final int BLACK = 1;
    private final int EMPTY = -1;
    private ImageIcon whitePiece;
    private ImageIcon blackPiece;

    //Colors
    private Color boardGreen = new Color(57, 126, 88);
    private Color lightBlack = new Color(32, 32, 32);
    private Color highlightYellow = new Color(255,255,102);

    //Fonts
    private Font titleFont = new Font("Ariel", Font.BOLD, 40);
    private Font playerFont = new Font("Ariel", Font.BOLD, 25);
    private Font scoreFont = new Font("Ariel", Font.BOLD, 20);
    private Font winnerFont = new Font("Ariel", Font.BOLD, 20);

    //Borders
    private Border gridBorder = new LineBorder(Color.WHITE, 1);


    public UserInterface(){

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

        //Frame for Reversi
        JFrame frame = new JFrame("Reversi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(lightBlack);

        //Set layout manager for board
        MigLayout mig = new MigLayout("","[center]","");
        frame.setLayout(mig);

        //Set game title
        JLabel title = new JLabel("REVERSI");
        title.setForeground(Color.WHITE);
        title.setFont(titleFont);

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


        //Panel containing game information
        JPanel infoPanel = new JPanel();
        infoPanel.setSize(new Dimension(400,50));

        //Set layout manager for information Panel
        MigLayout mig2 = new MigLayout("",
                "[100,center][190,center][100,center]",
                "[][]");
        infoPanel.setLayout(mig2);
        infoPanel.setBackground(lightBlack);

        //Label indicating black player
        JLabel blackPlayer = new JLabel("<HTML><U>Black</U></HTML>");
        blackPlayer.setForeground(Color.WHITE);
        blackPlayer.setFont(playerFont);

        //Label indicating black player score
        JLabel blackScore = new JLabel("2");
        blackScore.setForeground(Color.WHITE);
        blackScore.setFont(scoreFont);

        //Label indicating white player
        JLabel whitePlayer = new JLabel("<HTML><U>White</U></HTML>");
        whitePlayer.setForeground(Color.WHITE);
        whitePlayer.setFont(playerFont);

        //Label indicating white player score
        JLabel whiteScore = new JLabel("2");
        whiteScore.setForeground(Color.WHITE);
        whiteScore.setFont(scoreFont);

        //Label indicating game information such as who's turn, winner, loser, etc...
        JLabel infoLabel = new JLabel();
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(winnerFont);

        //Add Components to information panel
        infoPanel.add(blackPlayer);
        infoPanel.add(infoLabel, "span 1 2");
        infoPanel.add(whitePlayer, "wrap");
        infoPanel.add(blackScore);
        infoPanel.add(whiteScore);

        //Add all Components to game frame
        frame.add(title, "wrap");
        frame.add(boardPanel, "wrap");
        frame.add(infoPanel);
        frame.setVisible(true);
        frame.pack();
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
                while (board[row += xIncrement][col += yIncrement].getStatus() != diskColor) {
                    board[row][col].setIcon(disk);
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


}
