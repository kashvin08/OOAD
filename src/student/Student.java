package student;

import core.User;
import javax.swing.JPanel;
import javax.swing.JLabel;
import static core.User.UserRole.STUDENT;
import shared.Constants;

public class Student extends User {

    private String title, abstractTxt, supervisor, presentationType, filePath;

    public Student(String id, String name, String email, String password) {
        super(id, name, email, password, STUDENT);
    }

    @Override
    public String getDashboardTitle() {
        return "Student Dashboard: " + name;
    }

    @Override
    public JPanel getDashboardPanel() {
    // This passes the User.UserRole to StudentGUI
    return new student.StudentGUI(this.getUserID(), this.getRole()); 
}

    public void register(String t, String abs, String sup, String type) {
        this.title = t;
        this.abstractTxt = abs;
        this.supervisor = sup;
        this.presentationType = type;
    }

    public void uploadFile(String path) {
        this.filePath = path;
    }

    public String toFileString() {
    return userID + Constants.DELIMITER + name + Constants.DELIMITER + 
           title + Constants.DELIMITER + presentationType + Constants.DELIMITER + filePath;
}
}