package evaluator;

import core.User;
import javax.swing.JPanel;
import javax.swing.JLabel;
import static core.User.UserRole.EVALUATOR;


public class Evaluator extends User {

    //Constructor for Evaluator.
    public Evaluator(String userID, String name, String email, String password) {
        super(userID, name, email, password, UserRole.EVALUATOR);
    }

    @Override
    public String getDashboardTitle() {
        return "Evaluator Dashboard - " + getName();
    } //Returns the title for the Main Window when this user logs in.

    @Override
    public JPanel getDashboardPanel() {
        return new EvaluatorGUI(this);
    } //Returns the GUI Panel for this specific user.
}

