package core;

import shared.Constants;
import javax.swing.*;
import java.awt.*;

public class LoginSystem {

    private JFrame loginFrame;
    private JTextField txtUserID;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private MainApplication mainApp;

    public LoginSystem(MainApplication mainApp) {
        this.mainApp = mainApp;
        createLoginGUI();
    }

    private void createLoginGUI() {//login GUI
        loginFrame = new JFrame(Constants.APP_TITLE + " - Login");
        loginFrame.setSize(400, 350);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new BorderLayout());
        loginFrame.setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Constants.PRIMARY_COLOR);
        JLabel lblTitle = new JLabel(Constants.APP_TITLE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        txtUserID = new JTextField(15);
        formPanel.add(txtUserID, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        formPanel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;

        cmbRole = new JComboBox<>(new String[] {
                "Select Role",
                MainApplication.UserRole.STUDENT.name(),
                MainApplication.UserRole.EVALUATOR.name(),
                MainApplication.UserRole.COORDINATOR.name()
        });
        formPanel.add(cmbRole, gbc);

        JPanel buttonPanel = new JPanel();
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");

        btnLogin.addActionListener(e -> performLogin());
        btnRegister.addActionListener(e -> showRegistration());

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        loginFrame.add(headerPanel, BorderLayout.NORTH);
        loginFrame.add(formPanel, BorderLayout.CENTER);
        loginFrame.setVisible(true);
    }

    private void performLogin() {
        String userID = txtUserID.getText().trim();
        String password = new String(txtPassword.getPassword());
        String roleText = (String) cmbRole.getSelectedItem();

        if (userID.isEmpty() || password.isEmpty() || roleText.equals("Select Role")) {
            JOptionPane.showMessageDialog(loginFrame,
                    "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MainApplication.UserRole selectedRole;
        try {
            selectedRole = MainApplication.UserRole.valueOf(roleText);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(loginFrame,
                    "Invalid role selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //authenticate using hashed password
        String[] userData = FileHandler.authenticate(userID, password);

        if (userData != null &&
            MainApplication.UserRole.valueOf(userData[4]).equals(selectedRole)) {

            JOptionPane.showMessageDialog(loginFrame,
                    "Login successful! Welcome " + userData[1],
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loginFrame.dispose();
            mainApp.showDashboard(userID, userData[1], selectedRole);

        } else {
            JOptionPane.showMessageDialog(loginFrame,
                    "Invalid credentials or role mismatch!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistration() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        JTextField txtID = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();

        JComboBox<String> cmbRegRole = new JComboBox<>(
                new String[]{
                        MainApplication.UserRole.STUDENT.name(),
                        MainApplication.UserRole.EVALUATOR.name(),
                        MainApplication.UserRole.COORDINATOR.name()
                });

        panel.add(new JLabel("User ID:"));//details
        panel.add(txtID);
        panel.add(new JLabel("Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Password:"));
        panel.add(txtPass);
        panel.add(new JLabel("Role:"));
        panel.add(cmbRegRole);

        int result = JOptionPane.showConfirmDialog(loginFrame, panel,
                "Register New User", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            String hashedPassword = FileHandler.hashPassword(new String(txtPass.getPassword()));

            String record = String.join(Constants.DELIMITER,
                    txtID.getText().trim(),
                    txtName.getText().trim(),
                    txtEmail.getText().trim(),
                    hashedPassword, //save hashed password
                    cmbRegRole.getSelectedItem().toString()
            );

            FileHandler.appendToFile(Constants.USERS_FILE, record);

            JOptionPane.showMessageDialog(loginFrame,
                    "Registration successful! You can now login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void show() {
        loginFrame.setVisible(true);
    }
}