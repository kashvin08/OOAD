import javax.swing.*;
import java.awt.*;

public class StudentGUI extends JFrame {

    JButton regBtn, uploadBtn;

    public StudentGUI() {
        setTitle("Student Dashboard");
        setLayout(new FlowLayout());

        regBtn = new JButton("Register Seminar");
        uploadBtn = new JButton("Upload Presentation");

        add(regBtn);
        add(uploadBtn);

        regBtn.addActionListener(e -> new RegistrationForm());
        uploadBtn.addActionListener(e -> new UploadForm());

        setSize(300,150);
        setVisible(true);
    }
}
