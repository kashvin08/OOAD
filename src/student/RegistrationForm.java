package student;

import shared.Constants;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class RegistrationForm extends JFrame {

    private JTextField titleField, supervisorField;
    private JTextArea abstractArea;
    private JComboBox<String> typeCombo;
    private JButton submitBtn;
    private String studentID;
    private String studentName;

    public RegistrationForm(String userID, String userName) {
        this.studentID = userID;
        this.studentName = userName;

        setTitle("Seminar Registration - " + studentName + " (" + studentID + ")");
        setLayout(new BorderLayout(10, 10));
        
        //header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Constants.PRIMARY_COLOR);
        JLabel lblHeader = new JLabel("REGISTER NEW SEMINAR");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(lblHeader);

        //form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleField = new JTextField(20);
        abstractArea = new JTextArea(5, 20);
        abstractArea.setLineWrap(true);
        abstractArea.setWrapStyleWord(true);
        supervisorField = new JTextField(20);
        typeCombo = new JComboBox<>(new String[]{"Oral", "Poster"});
        submitBtn = new JButton("Submit Registration");
        submitBtn.setBackground(Constants.PRIMARY_COLOR);
        submitBtn.setForeground(Color.WHITE);

        //adding components to gridbag for dynamic arrangement
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Project Title:"), gbc);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Supervisor Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(supervisorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Presentation Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Abstract:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(abstractArea), gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.add(submitBtn);

        //submit actn
        submitBtn.addActionListener(e -> performSubmission());

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(500, 450);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void performSubmission() {
        String title = titleField.getText().trim();
        String supervisor = supervisorField.getText().trim();
        String type = typeCombo.getSelectedItem().toString(); 
        String abstractTxt = abstractArea.getText().trim().replace("|", ""); 

        if (title.isEmpty() || supervisor.isEmpty() || abstractTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        //board id for poster submissions
        String boardID = "N/A";
        if (type.equalsIgnoreCase("Poster")) {
            boardID = "B-" + (new Random().nextInt(900) + 100); // e.g., B-452
        }

        String record = String.join(Constants.DELIMITER, 
            studentID, studentName, title, supervisor, type, abstractTxt, "NONE", "PENDING", boardID
        );

        core.FileHandler.appendToFile(Constants.SUBMISSIONS_FILE, record);
        JOptionPane.showMessageDialog(this, "Seminar Registered Successfully!\nBoard ID: " + boardID);
        this.dispose();
    }
}