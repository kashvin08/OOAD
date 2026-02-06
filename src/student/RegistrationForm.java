package student;

import shared.Constants;
import core.MainApplication;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegistrationForm extends JPanel {
    private JTextField title, supervisor;
    private JTextArea abstractTxt;
    private JComboBox<String> type;

    public RegistrationForm() {
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(30, 40, 30, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 10, 10, 10);

        JLabel head = new JLabel("SEMINAR REGISTRATION");
        head.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0; card.add(head, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; card.add(new JLabel("Title:"), gbc);
        title = new JTextField(20); gbc.gridx = 1; card.add(title, gbc);

        gbc.gridx = 0; gbc.gridy = 2; card.add(new JLabel("Supervisor:"), gbc);
        supervisor = new JTextField(20); gbc.gridx = 1; card.add(supervisor, gbc);

        gbc.gridx = 0; gbc.gridy = 3; card.add(new JLabel("Type:"), gbc);
        type = new JComboBox<>(new String[]{"Oral", "Poster"}); gbc.gridx = 1; card.add(type, gbc);

        gbc.gridx = 0; gbc.gridy = 4; card.add(new JLabel("Abstract:"), gbc);
        abstractTxt = new JTextArea(5, 20); gbc.gridx = 1; card.add(new JScrollPane(abstractTxt), gbc);

        JButton sub = new JButton("SUBMIT");
        sub.setBackground(Constants.COLOR_ACCENT); sub.setForeground(Color.WHITE);
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 5; card.add(sub, gbc);

        sub.addActionListener(e -> {
            MainApplication main = (MainApplication) SwingUtilities.getWindowAncestor(this);
            Student s = new Student(main.getCurrentUserID(), "Name", "email@uni.edu", "pass");
            s.register(title.getText(), abstractTxt.getText(), supervisor.getText(), (String)type.getSelectedItem());
            new StudentController().saveStudent(s);
            JOptionPane.showMessageDialog(this, "Submitted!");
        });

        add(card, BorderLayout.CENTER);
    }
}