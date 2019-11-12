/*Created along with the MainTest class in the Server module just to
* test both modules building and running simultaneously. */
import javax.swing.*;

public class MainTest {

    public MainTest(){
        JFrame frame = new JFrame("Test Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);

        JPanel panel = new JPanel();
        frame.add(panel);
    }
    public static void main(String[] args) {
        MainTest main = new MainTest();
    }
}
