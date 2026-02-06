package evaluator;

import core.User;
import javax.swing.JPanel;

public class Evaluator extends User {

    //constr for evaluator.
    public Evaluator(String userID, String name, String email, String password) {
        super(userID, name, email, password, UserRole.EVALUATOR);
    }

    @Override
    public String getDashboardTitle() {
        return "Evaluator Dashboard - " + getName();
    } //returns title for main window when this user logs in.

    @Override
    public JPanel getDashboardPanel() {
        return new EvaluatorGUI(this);
    } //returns GUI Panel for this specific user.
}