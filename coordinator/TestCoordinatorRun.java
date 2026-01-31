package modules.coordinator;

import shared.Constants;
import javax.swing.JFrame;

public class TestCoordinatorRun {
    public static void main(String[] args) {
        // Simulate a main window (JFrame)
        JFrame frame = new JFrame("Coordinator Page");
        frame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        //Load　Dashboard
        CoordinatorGUI myPanel = new CoordinatorGUI();
        frame.add(myPanel);
        frame.setVisible(true);
    }
}