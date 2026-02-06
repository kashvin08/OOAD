package coordinator;

import core.User;
import javax.swing.JPanel;

public class Coordinator extends User {
    public Coordinator(String userID, String name, String email, String password) {
        // Call
        super(userID, name, email, password, UserRole.COORDINATOR);
    }
    @Override
    public String getDashboardTitle() {
        return "Coordinator Dashboard - " + this.name;
    }
    @Override
    public JPanel getDashboardPanel() {
        // Return the GUI panel you are responsible for
        return new CoordinatorGUI();
    }
    public void approveSchedule() {
        System.out.println("Schedule approved by " + getName());
    }
}
