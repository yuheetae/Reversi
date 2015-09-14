package yu.heetae.reversi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable{

    private static String host = "192.168.1.16";
    private static int port = 7077;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Thread outputThread;

    private final int WHITE = 0;
    private final int BLACK = 1;
    private final int EMPTY = -1;

    private UserInterface ui;
    private GameLogic logic;

    private Color highlightYellow = new Color(255,255,102);
    private Color boardGreen = new Color(57, 126, 88);

    private boolean isPlayerTurn;
    private boolean isTieGame;

    public Client() {
        ui = new UserInterface();
    }

    @Override
    public void run() {

        try {
            socket = new Socket(host, port);
            System.out.println("CONNECTED");
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            int clientType = ois.readInt();
            System.out.println("client type: " + clientType);

            logic = new GameLogic(clientType);
            if(clientType == 0) {
                ui.setInfoLabel("Opponent Turn");
                ui.disableButtons();
            }
            else if(clientType == 1) {
                startTurn();
            }
            ui.setListeners(new buttonListener(), new mouseListener());

            while(true) {
                Object receivedMessage = ois.readObject();
                if(receivedMessage instanceof String) {
                    Object receivedMessage2 = ois.readObject();
                    String gameOver = (String) receivedMessage;
                    int[] opponentMoves = (int[]) receivedMessage2;
                }
                else {
                    Message message = (Message) receivedMessage;
                    if(message != null) {
                        int row = message.getRow();
                        int col = message.getCol();
                        boolean[] directionsToFlip = (boolean[]) message.getDirectionsToFlip();
                        String mess = message.getMessage();

                        logic.flipDisks(directionsToFlip, row, col, logic.getOpponentColor());
                        ui.flip(directionsToFlip, row, col, logic.getOpponentColor());
                        ui.setScore(logic.getWhiteScore(), logic.getBlackScore());

                        if(message.getMessage() == "tie") {
                            endGame();
                        }

                    }
                    else if(message == null) {
                        isTieGame = true;
                    }

                    if(logic.getWhiteScore() + logic.getBlackScore() == 64) {
                        endGame();
                    }
                    else {
                        startTurn();
                    }
                }
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int[] readMessage() {
        int[] array = null;
        try {
            array = (int[])ois.readObject();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return array;
    }

    public void sendMessage(Message message) {
        try {
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public class buttonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //Get the square being clicked
            JButton button = (JButton) e.getSource();
            int i = (int) button.getClientProperty("column");
            int j = (int) button.getClientProperty("row");

            Square square = logic.getSquare(i, j);

            int color = logic.getPlayerColor();


            if(square.isValidMove()) {
                boolean[] directionToFlip = logic.getDirectionsToFlip(i, j);
                logic.flipDisks(directionToFlip, i, j, logic.getPlayerColor());
                ui.flip(directionToFlip, i, j, logic.getPlayerColor());
                ui.setScore(logic.getWhiteScore(), logic.getBlackScore());

                endTurn(new Message(i, j, directionToFlip, null));

                if(logic.getWhiteScore() + logic.getBlackScore() == 64) {
                    endGame();
                }
            }
        }
    }

    public class mouseListener implements MouseListener {

        int i;
        int j;
        Square square;

        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            i = (int) button.getClientProperty("column");
            j = (int) button.getClientProperty("row");

            square = logic.getSquare(i, j);

            if(square.isValidMove() && isPlayerTurn) {
                e.getComponent().setBackground(highlightYellow);
            }
            else {
                return;
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(square.isValidMove() && isPlayerTurn) {
                e.getComponent().setBackground(boardGreen);
            }
            else {
                return;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub
            e.getComponent().setBackground(boardGreen);
        }
    }

    private void flip(boolean[] directionsToFlip) {

    }

    private void startTurn() {
        isPlayerTurn = true;
        int numberOfMoves = logic.checkForMoves();
        if(numberOfMoves == 0) {
            System.out.println("No possible moves");
            if(isTieGame == true) {
                sendMessage(new Message(0, 0, null, "tie"));
                endGame();
            }
            else {
                isTieGame = false;
                endTurn(null);
            }
        }
        else {
            ui.setInfoLabel("Your Turn");
            ui.enableButtons();
        }
    }

    private void endTurn(Message message) {
        isPlayerTurn = false;
        ui.disableButtons();
        ui.setInfoLabel("Opponent Turn");
        sendMessage(message);
    }

    private void endGame() {

        int playerScore;
        int opponentScore;

        if(logic.getPlayerColor() == WHITE) {
            playerScore = logic.getWhiteScore();
            opponentScore = logic.getBlackScore();
        }
        else {
            playerScore = logic.getBlackScore();
            opponentScore = logic.getWhiteScore();
        }

        if(playerScore == opponentScore) ui.setInfoLabel("Tie");
        else if(playerScore > opponentScore) ui.setInfoLabel("You Win");
        else if(playerScore < opponentScore) ui.setInfoLabel("You Lose");


        /*
        int response = JOptionPane.showConfirmDialog(null, "Would you like to play again?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(response == JOptionPane.NO_OPTION) {
            logic.resetBoard();
            ui.resetBoard();
        }
        */
    }


    public static void main(String[] args) throws IOException {
        Client client = new Client();
        new Thread(client).start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("In shutdown hook");
            }
        }, "Shutdown-thread"));
    }

}