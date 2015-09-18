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

    private int playerColor;
    private int opponentColor;

    private boolean noPossibleMoves = false;

    private int clientType;

    public Client() {
        ui = new UserInterface();
        ui.setListeners(new buttonListener(), new mouseListener());
    }

    @Override
    public void run() {

        try {
            //Create socket and input/output streams
            socket = new Socket(host, port);
            System.out.println("CONNECTED");
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            //server notifies user of clientType; 0 = white player, 1 = black player, -1 = observer
            clientType = ois.readInt();
            System.out.println("client type: " + clientType);

            //Create new logic object and get player/opponent colors
            logic = new GameLogic(clientType);
            setPlayerColor(logic.getPlayerColor());
            setOpponentColor(logic.getOpponentColor());

            //if player is white disable buttons and wait for opponent's turn
            if(clientType == 0) {
                ui.setInfoLabel("Opponent Turn");
                ui.disableButtons();
            }

            //if player is black start turn
            else if(clientType == 1) {
                startTurn();
            }

            //Receive messages from server and process them
            while (true) {
                Object receivedMessage = ois.readObject();

                if(receivedMessage == null) continue;

                //If user is an observer that just connected retrieve current board state from server
                else if(receivedMessage instanceof Square[][]) {
                    ui.setBoard((Square[][]) receivedMessage);
                }

                else {
                    Message message = (Message) receivedMessage;

                    //if user is an observer
                    if(clientType == -1) {
                       processObserverMove(message);
                    }
                    //If user is playing
                    else {
                        processPlayerMove(message);
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


    //Input opponent's move and decide whether to start turn or end game
    public void processPlayerMove(Message m) {

        //Set Data from Message
        int row = m.getRow();
        int col = m.getCol();
        boolean[] directionsToFlip = (boolean[]) m.getDirectionsToFlip();
        String message = m.getMessage();

        //if message contains a directionToFlip, flip pieces
        if(directionsToFlip != null) {

            //flip disks in logic and ui
            logic.flipDisks(directionsToFlip, row, col, opponentColor);
            ui.flip(directionsToFlip, row, col, opponentColor);

            //Set score in ui
            ui.setScore(logic.getWhiteScore(), logic.getBlackScore());
        }

        //if opponent has no moves
        if(message == "opponent has no moves") {
            noPossibleMoves = true;
        }
        //if opponent and player have no moves end game
        else if(message == "no more moves") {
            endGame();
        }

        //if the board is full the game is over
        if(logic.getWhiteScore() + logic.getBlackScore() == 64) {
            endGame();
        }
        //else start users turn
        else {
            startTurn();
        }

    }

    public void processObserverMove(Message m) {

        //Set Data from Message
        int row = m.getRow();
        int col = m.getCol();
        boolean[] directionsToFlip = (boolean[]) m.getDirectionsToFlip();
        String message = m.getMessage();
        int color = m.getColor();
        int whiteScore = m.getWhiteScore();
        int blackScore = m.getBlackScore();

        //if message contains a directionToFlip, flip pieces
        if(directionsToFlip != null) {
            ui.flip(directionsToFlip, row, col, color);
            //Set score in ui
            ui.setScore(whiteScore, blackScore);
        }

        if(whiteScore + blackScore == 64 || message == "no more moves") {
            if(whiteScore > blackScore) ui.setInfoLabel("White Wins");
            else if(whiteScore < blackScore) ui.setInfoLabel("Black Wins");
            else ui.setInfoLabel("Tie");
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

            if(square.isValidMove()) {
                boolean[] directionToFlip = logic.getDirectionsToFlip(i, j);
                logic.flipDisks(directionToFlip, i, j, playerColor);
                ui.flip(directionToFlip, i, j, playerColor);
                ui.setScore(logic.getWhiteScore(), logic.getBlackScore());

                endTurn(new Message(i, j, directionToFlip, null, null, playerColor));

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
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(square.isValidMove() && isPlayerTurn) {
                e.getComponent().setBackground(boardGreen);
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
        //Indicate it is users turn
        isPlayerTurn = true;

        //update board's valid moves and return # of possible moves
        int numberOfMoves = logic.checkForMoves();

        //if no more possible moves...
        if(numberOfMoves == 0) {
            System.out.println("No possible moves");

            //opponent has no moves AND player has no moves
            if(noPossibleMoves == true) {
                sendMessage(new Message(0, 0, null, "no more moves", null, 0));
                endGame();
            }

            //opponent has no moves BUT player has moves
            else {
                sendMessage(new Message(0, 0, null, "opponent has no moves", null, 0));
            }
        }

        //if there are possible moves enable buttons
        else {
            ui.setInfoLabel("Your Turn");
            ui.enableButtons();
        }
    }

    private void endTurn(Message message) {
        //Indicate it is not users turn
        isPlayerTurn = false;

        //Disable buttons & set info label

        ui.setInfoLabel("Opponent Turn");

        //Send message to server
        sendMessage(message);
    }

    private void endGame() {

        int playerScore;
        int opponentScore;

        //set scores corresponding to piece color
        if(playerColor == WHITE) {
            playerScore = logic.getWhiteScore();
            opponentScore = logic.getBlackScore();
        }
        else {
            playerScore = logic.getBlackScore();
            opponentScore = logic.getWhiteScore();
        }

        //disable buttons
        ui.disableButtons();

        //Determine outcome of game
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

    public int getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(int playerColor) {
        this.playerColor = playerColor;
    }

    public int getOpponentColor() {
        return opponentColor;
    }

    public void setOpponentColor(int opponentColor) {
        this.opponentColor = opponentColor;
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        new Thread(client).start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("In shutdown hook");
                //client.sendMessage(new Message(0, 0, null, "disconnect", null, 0));
            }
        }, "Shutdown-thread"));
    }

}