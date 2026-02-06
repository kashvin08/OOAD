package student;

import javax.swing.*;
import java.awt.*;

public class StudentGUI extends JFrame {

    JButton regBtn, uploadBtn;
    private String currentUserID;
    private String currentUserName;

    public StudentGUI(String userID, String userName) {
        this.currentUserID = userID;
        this.currentUserName = userName;

        setTitle("Student Dashboard - " + userName);
        setLayout(new FlowLayout());

        regBtn = new JButton("Register Seminar");
        uploadBtn = new JButton("Upload Presentation");

        add(regBtn);
        add(uploadBtn);

        regBtn.addActionListener(e -> new RegistrationForm(currentUserID, currentUserName));
        uploadBtn.addActionListener(e -> new UploadForm(currentUserID, currentUserName));

        setSize(300, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}