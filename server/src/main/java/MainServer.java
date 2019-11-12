/*Created along with the MainClient class in the Client module just to
 * check both modules can build and run simultaneously. */

import javax.swing.*;

public class MainServer {

    public MainServer(){
        JFrame frame = new JFrame("Test Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);

        JPanel panel = new JPanel();
        frame.add(panel);
    }
    public static void main(String[] args) {
        MainServer main = new MainServer();
    }
}
