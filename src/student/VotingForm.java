package student;

import shared.Constants;
import core.FileHandler;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class VotingForm extends JFrame {
    private JComboBox<String> nomineeCombo;
    private String voterID;

    public VotingForm(String voterID) {
        this.voterID = voterID;

        if (hasAlreadyVoted()) {
            JOptionPane.showMessageDialog(null, "You have already cast your vote! Only one vote per student is allowed.", "Already Voted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setTitle("People's Choice - Cast Your Vote");
        setSize(450, 250);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Constants.COLOR_BACKGROUND);
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Vote for your favorite peer presentation:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        add(titleLabel, gbc);

        nomineeCombo = new JComboBox<>();
        loadNominees();
        gbc.gridy = 1;
        add(nomineeCombo, gbc);

        JButton btnVote = new JButton("Submit My Vote");
        btnVote.setBackground(Constants.PRIMARY_COLOR);
        btnVote.setForeground(Color.WHITE);
        btnVote.setFont(new Font("Arial", Font.BOLD, 13));
        
        btnVote.addActionListener(e -> submitVote());
        gbc.gridy = 2;
        add(btnVote, gbc);

        setVisible(true);
    }

    private void loadNominees() {
        List<String> submissions = FileHandler.readAllLines(Constants.SUBMISSIONS_FILE);
        for (String line : submissions) {
            String[] data = line.split("\\" + Constants.DELIMITER);
            if (data.length >= 3) {
                nomineeCombo.addItem(data[1] + " | " + data[2] + " (" + data[0] + ")");
            }
        }
    }

    private boolean hasAlreadyVoted() {
        List<String> votes = FileHandler.readAllLines("votes.txt");
        for (String v : votes) {
            if (v.startsWith(voterID + Constants.DELIMITER)) {
                return true;
            }
        }
        return false;
    }

    private void submitVote() {
        String selected = (String) nomineeCombo.getSelectedItem();
        if (selected == null) return;

        String record = voterID + shared.Constants.DELIMITER + selected;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("votes.txt", true))) {
            bw.write(record);
            bw.newLine();
            bw.flush();
            
            System.out.println("Vote recorded in votes.txt: " + record); 
            JOptionPane.showMessageDialog(this, "Vote cast successfully!");
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving vote: " + ex.getMessage());
        }
    }
}