package core;

import javax.swing.JPanel;

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
