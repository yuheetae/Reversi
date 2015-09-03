package yu.heetae.reversi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by yu on 8/26/15.
 */
public class Controller {

    private final String MY_TURN = "yu.heetae.reversi.my_turn";
    private final String OPPONENT_TURN = "yu.heetae.reversi.opponent_turn";
    private UserInterface ui;
    private GameLogic logic;

    public Controller() {
        this.ui = new UserInterface();
        this.logic = new GameLogic(1);
    }

    public static void main(String args[]) {
        //UserInterface ui = new UserInterface();
    }

    public class listener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //Get the square being clicked
            Square square = (Square) e.getSource();

            //If the square is not a valid move return
            if (!square.isValidMove()) return;

            //get square's index from client property
            int row = (int) square.getClientProperty("row");
            int col = (int) square.getClientProperty("column");

            logic.flipDisks(row, col, 1);

        }
    }

}
