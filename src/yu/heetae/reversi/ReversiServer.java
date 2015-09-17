package yu.heetae.reversi;

/**
 * Created by yu on 8/26/15.
 */
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ReversiServer {

    private static ArrayList<ServerHandler> clientList;
    private static GameLogic logic = new GameLogic(-1);

    public static void main(String[] args) {
        clientList = new ArrayList<ServerHandler>();
        try {
            int i=0;
            ServerSocket s = new ServerSocket(7077);

            while(true) {
                Socket client = s.accept();
                ServerHandler ServerHandler = new ReversiServer().new ServerHandler(client);
                if(i==0) ServerHandler.setClientStatus(0);
                else if(i==1) ServerHandler.setClientStatus(1);
                else ServerHandler.setClientStatus(-1);
                clientList.add(ServerHandler);
                System.out.println("Spawning connection number" + i);
                System.out.println(ServerHandler.getClientStatus());
                Runnable r = ServerHandler;
                Thread t = new Thread(r);
                t.start();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ServerHandler implements Runnable {

        private Socket incoming;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private int clientStatus = -1;	//if -1 client is an observer, if 0 client is player 1, if 1 client is player 2

        public ServerHandler(Socket s) {
            incoming = s;
            try {
                ois = new ObjectInputStream(incoming.getInputStream());
                oos = new ObjectOutputStream(incoming.getOutputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }

        public void setClientStatus(int status) {
            this.clientStatus = status;
        }

        public int getClientStatus() {
            return clientStatus;
        }


        @Override
        public void run() {
            try {
                //Notify client if they are playing or observing
                oos.writeInt(getClientStatus());
                oos.flush();

                //If client is observing send the current board state
                if(getClientStatus() == -1) {
                    //oos.writeObject(new Message(0, 0, null, "initial state", logic.getBoard(), 0));
                    oos.writeObject(logic.getBoard());
                    oos.flush();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Listen for Messages
            while(true) {
                try {
                    Object message = ois.readObject();

                    if(message instanceof Integer){
                        clientList.remove(this);
                        if(getClientStatus()==0 || getClientStatus()==1) {
                            clientList.get(0).setClientStatus(0);
                            clientList.get(1).setClientStatus(1);
                        }
                    }
                    else {
                        Message m = (Message) message;
                        if(m.getDirectionsToFlip() != null) {
                            logic.flipDisks(m.getDirectionsToFlip(), m.getRow(), m.getCol(), m.getColor());
                        }
                        sendToAll(m, this);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    break;
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    public void sendToAll(Message message, ServerHandler sh) {

        for(ServerHandler client : clientList) {
            try {
                if(client != sh) {
                    client.oos.writeObject(message);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //client.out(message);
        }
    }



}
