package yu.heetae.reversi;

import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;

/**
 * Created by yu on 8/26/15.
 */
public class UserInterface {

    private JButton[][] board = new JButton[8][8];
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

    private JLabel whiteScore;
    private JLabel blackScore;
    private JLabel infoLabel;

    //Borders
    private Border gridBorder = new LineBorder(Color.WHITE, 1);

    //array containing x increment values for each direction, e.g. to check north increment by -1 in x direction
    private int[] xDirection = {0, 1, 0, -1, 1, -1, 1, -1};
    //array containing y increment values for each direction
    private int[] yDirection = {-1, 0, 1, 0, -1, -1, 1, 1};

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
                board[i][j] = new JButton();
                board[i][j].setBackground(boardGreen);
                board[i][j].setOpaque(true);

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
                board[i][j].putClientProperty("column", i);
                board[i][j].putClientProperty("row", j);


            }
        }

        board[3][3].setDisabledIcon(whitePiece);
        board[4][4].setDisabledIcon(whitePiece);
        board[3][4].setDisabledIcon(blackPiece);
        board[4][3].setDisabledIcon(blackPiece);

        board[3][3].setIcon(whitePiece);
        board[4][4].setIcon(whitePiece);
        board[3][4].setIcon(blackPiece);
        board[4][3].setIcon(blackPiece);

        board[3][3].setDisabledIcon(whitePiece);
        board[4][4].setDisabledIcon(whitePiece);
        board[3][4].setDisabledIcon(blackPiece);
        board[4][3].setDisabledIcon(blackPiece);

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
        blackScore = new JLabel("2");
        blackScore.setForeground(Color.WHITE);
        blackScore.setFont(scoreFont);

        //Label indicating white player
        JLabel whitePlayer = new JLabel("<HTML><U>White</U></HTML>");
        whitePlayer.setForeground(Color.WHITE);
        whitePlayer.setFont(playerFont);

        //Label indicating white player score
        whiteScore = new JLabel("2");
        whiteScore.setForeground(Color.WHITE);
        whiteScore.setFont(scoreFont);

        //Label indicating game information such as who's turn, winner, loser, etc...
        infoLabel = new JLabel();
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


    public void flip(boolean[] directionsToFlip, int row, int col, int color) {

        ImageIcon piece;

        if (color == WHITE) piece = whitePiece;
        else piece = blackPiece;

        //temp variables to reset row and col to original values
        int originalRow = row;
        int originalCol = col;

        //update square's status and make it a non valid move
        board[row][col].setIcon(piece);
        board[row][col].setDisabledIcon(piece);
        board[row][col].setEnabled(false);
        board[row][col].setBackground(boardGreen);

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
                while(board[row += yIncrement][col += xIncrement].getIcon() != piece) {
                    board[row][col].setIcon(piece);
                    board[row][col].setEnabled(false);
                    board[row][col].setDisabledIcon(piece);
                }

            }
        }
    }

    public void setScore(int whiteScore, int blackScore) {
        this.whiteScore.setText(Integer.toString(whiteScore));
        this.blackScore.setText(Integer.toString(blackScore));
    }


    public void setListeners(ActionListener actionListener, MouseListener mouseListener) {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                board[i][j].addActionListener(actionListener);
                board[i][j].addMouseListener(mouseListener);
            }
        }
    }

    public void setInfoLabel(String info) {
        switch(info) {
            case "Your Turn":
                infoLabel.setText("<html><div style=\"text-align: center;\">" + "YOUR TURN" + "</html>");
                break;
            case "Opponent Turn":
                infoLabel.setText("<html><div style=\"text-align: center;\">" + "OPPONENT'S<br>TURN" + "</html>");
                break;
            case "You Lose":
                infoLabel.setText("<html><div style=\"text-align: center;\">" + "GAME OVER<br>YOU LOSE" + "</html>");
                break;
            case "You Win":
                infoLabel.setText("<html><div style=\"text-align: center;\">" + "GAME OVER<br>YOU WIN" + "</html>");
                break;
            case "Black Wins":
                infoLabel.setText("<html><div style=\"text-align: center;\">" + "GAME OVER<br>BLACK WINS" + "</html>");
                break;
            case "White Wins":
                infoLabel.setText("<html><div style=\"text-align: center;\">" + "GAME OVER<br>WHITE WINS" + "</html>");
                break;
            case "Tie":
                infoLabel.setText("<html><div style=\"text-align: center;\">" + "GAME OVER<br>TIE GAME" + "</html>");
                break;
            default:
                infoLabel.setText(null);
        }
    }

    public void setBoard(Square[][] squares) {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                if(squares[i][j].getStatus() == WHITE) {
                    board[i][j].setDisabledIcon(whitePiece);
                    board[i][j].setIcon(whitePiece);
                }
                else if(squares[i][j].getStatus() == BLACK) {
                    board[i][j].setDisabledIcon(blackPiece);
                    board[i][j].setIcon(blackPiece);
                }
            }
        }
    }

    public void resetBoard() {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                board[i][j].setIcon(null);
                board[i][j].setDisabledIcon(null);
            }
        }
        board[3][3].setDisabledIcon(whitePiece);
        board[4][4].setDisabledIcon(whitePiece);
        board[3][4].setDisabledIcon(blackPiece);
        board[4][3].setDisabledIcon(blackPiece);

        board[3][3].setIcon(whitePiece);
        board[4][4].setIcon(whitePiece);
        board[3][4].setIcon(blackPiece);
        board[4][3].setIcon(blackPiece);

        board[3][3].setDisabledIcon(whitePiece);
        board[4][4].setDisabledIcon(whitePiece);
        board[3][4].setDisabledIcon(blackPiece);
        board[4][3].setDisabledIcon(blackPiece);
    }

    public void enableButtons() {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                if(board[i][j].getIcon() != whitePiece || board[i][j].getIcon() != blackPiece) {
                    board[i][j].setEnabled(true);
                }
            }
        }
    }

    public void disableButtons() {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                board[i][j].setEnabled(false);
            }
        }
    }


}
