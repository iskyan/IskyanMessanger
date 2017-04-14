import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by lab422 on 4/14/17.
 */

public class MessangerServer {

    private ArrayList<ObjectOutputStream> clientOutputStreams;

    public static void main(String[] args) {
        new MessangerServer().go();
    }

    private void go() {
        clientOutputStreams = new ArrayList<>();
        try {
            ServerSocket serverSock = new ServerSocket(4242);
            System.out.println("Server started...");
            while (true) {
                Socket clientSocket = serverSock.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputStreams.add(out);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void tellEveryone(Object o) {
        for (Object clientOutputStream : clientOutputStreams) {
            try {
                ObjectOutputStream out = (ObjectOutputStream) clientOutputStream;
                out.writeObject(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientHandler implements Runnable {

        private ObjectInputStream in;
        private Socket clientSocket;

        ClientHandler(Socket socket) {
            try {
                clientSocket = socket;
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Object o;
            try {
                while ((o = in.readObject()) != null) {
                    tellEveryone(o);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
