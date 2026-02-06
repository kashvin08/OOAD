package student;

import shared.Constants;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class UploadForm extends JFrame {

    private String studentID;
    private String studentName;
    private JLabel statusLabel;
    private JButton chooseBtn;

    public UploadForm(String userID, String userName) {
        this.studentID = userID;
        this.studentName = userName;

        setTitle("Upload Materials - " + studentName);
        setSize(400, 250); // Slightly increased height for better layout
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        //header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Constants.PRIMARY_COLOR);
        JLabel lblHeader = new JLabel("PRESENTATION FILE UPLOAD");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(lblHeader);

        //center content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel infoLabel = new JLabel("Logged in as: " + studentName + " (" + studentID + ")");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        chooseBtn = new JButton("Select Presentation File");
        statusLabel = new JLabel("No file selected.");
        statusLabel.setForeground(Color.GRAY);

        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(infoLabel, gbc);

        gbc.gridy = 1;
        contentPanel.add(chooseBtn, gbc);

        gbc.gridy = 2;
        contentPanel.add(statusLabel, gbc);

        //btn actn
        chooseBtn.addActionListener(e -> handleFileUpload());

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void handleFileUpload() {
        JFileChooser fileChooser = new JFileChooser();
        
        //filter for pres formats
        fileChooser.setDialogTitle("Select PDF or Presentation File");
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            boolean success = updatePathInFile(studentID, path);
    
            if (success) {
                JOptionPane.showMessageDialog(this, "Presentation materials uploaded successfully!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not find your registration. Please register first.");
            }
        }
    }

    private boolean updatePathInFile(String studId, String newPath) {
        List<String> lines = core.FileHandler.readAllLines(Constants.SUBMISSIONS_FILE);
        boolean updated = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split("\\" + Constants.DELIMITER);
            
            //check if id matches (Index 0)
            if (parts.length >= 8 && parts[0].trim().equals(studId.trim())) {
                parts[6] = newPath; //update filepath
                
                //reconstruct line using the delimiter
                lines.set(i, String.join(Constants.DELIMITER, parts));
                updated = true;
                break;
            }
        }

        if (updated) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(Constants.SUBMISSIONS_FILE))) {
                for (String line : lines) {
                    pw.println(line);
                }
                return true;
            } catch (IOException e) {
                System.err.println("File Error: " + e.getMessage());
            }
        }
        return false;
    }
}