/*Created along with the MainServer class in the Server module just to
* check both modules can build and run simultaneously. */
import javax.swing.*;

public class MainClient {

    public MainClient(){
        JFrame frame = new JFrame("Test Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);

        JPanel panel = new JPanel();
        frame.add(panel);
    }
    public static void main(String[] args) {
        MainClient main = new MainClient();
    }
}
