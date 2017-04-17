import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by lab422 on 4/14/17.
 */

public class IskyanMessanger {

    private JFrame frame;
    private JTextField userMessage;
    private JList<String> incomingList;
    private Vector<String> listVector = new Vector<>();
    private String userName;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter your name: ");
        String name = reader.readLine();
        new IskyanMessanger().startUp(name);
    }

    private void startUp(String name) {
        userName = name;
        try {
            Socket sock = new Socket("", 4242);
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            Thread remote = new Thread(new RemoteReader());
            remote.start();
        } catch (Exception e) {
            System.out.println("Couldn't connect...");
        }
        buildGUI();
    }

    private void buildGUI() {

        frame = new JFrame("Iskyan Messanger");
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBackground(Color.darkGray);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Box buttonBox = new Box(BoxLayout.X_AXIS);
        incomingList = new JList<>();
        JScrollPane theList = new JScrollPane(incomingList);
        incomingList.setListData(listVector);

        userMessage = new JTextField(20);
        userMessage.addKeyListener(new MyEnterListener());
        buttonBox.add(userMessage);

        JButton send = new JButton("Send");
        send.addActionListener(new MySendListener());
        buttonBox.add(send);

        background.add(BorderLayout.CENTER, theList);
        background.add(BorderLayout.SOUTH, buttonBox);
        frame.getContentPane().add(background);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(300, 400);
        frame.setVisible(true);

    }

    public class MySendListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                out.writeObject(userName + ": " + userMessage.getText());
            } catch (Exception e) {
                System.out.println("Couldn't send it to the server...");
            }
            userMessage.setText("");
        }
    }

    public class MyEnterListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent keyEvent) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                try {
                    out.writeObject(userName + ": " + userMessage.getText());
                } catch (Exception e) {
                    System.out.println("Couldn't send it to the server...");
                }
                userMessage.setText("");
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {

        }
    }

    class RemoteReader implements Runnable {

        Object obj = null;

        @Override
        public void run() {
            try {
                while ((obj = in.readObject()) != null) {
                    String nameToShow = (String) obj;
                    listVector.add(nameToShow);
                    incomingList.setListData(listVector);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
