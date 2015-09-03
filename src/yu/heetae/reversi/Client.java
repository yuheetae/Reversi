package yu.heetae.reversi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Client implements Runnable{

    private static String host = "172.20.235.122";
    private static int port = 7077;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Thread outputThread;

    public Client() {

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


            while(true) {
                Object receivedMessage = ois.readObject();
                if(receivedMessage instanceof String) {
                    Object receivedMessage2 = ois.readObject();
                    String gameOver = (String) receivedMessage;
                    int[] opponentMoves = (int[]) receivedMessage2;
                }
                else {
                    int[] opponentMoves = (int[]) receivedMessage;
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

    public void sendMessage(int[] message) {
        try {
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {

    }

}