package coordinator;

import shared.Constants;
import javax.swing.JFrame;

public class TestCoordinatorRun {
    public static void main(String[] args) {//simulate a main window
        JFrame frame = new JFrame("Coordinator Page");
        frame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        //load　Dashboard
        CoordinatorGUI myPanel = new CoordinatorGUI();
        frame.add(myPanel);
        frame.setVisible(true);
    }
}