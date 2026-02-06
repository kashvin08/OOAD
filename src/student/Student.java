package student;

import core.User;
import javax.swing.*;

public class Student extends User {

    private String title;
    private String abstractTxt;
    private String supervisor;
    private String presentationType;
    private String filePath;

    public Student(String id, String name, String email, String password) {
        super(id, name, email, password, UserRole.STUDENT);
    }

    //reg info
    public void register(String title, String abstractTxt, String supervisor, String presentationType) {
        this.title = title;
        this.abstractTxt = abstractTxt;
        this.supervisor = supervisor;
        this.presentationType = presentationType;
    }

    public void uploadFile(String filePath) {
        this.filePath = filePath;
    }

    public String toFileString() {
        return getUserID() + "," + getName() + "," + title + "," +
               presentationType + "," + filePath;
    }

    //override abstract user mthods
    @Override
    public String getDashboardTitle() {
        return "Student Dashboard";
    }

    @Override
    public JPanel getDashboardPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Welcome to your dashboard, " + getName() + "!"));
        return panel;
    }
}
