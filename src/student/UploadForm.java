package student;

import shared.Constants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UploadForm extends JPanel {

    public UploadForm() {
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(new EmptyBorder(50, 50, 50, 50));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel lblTitle = new JLabel("UPLOAD PRESENTATION MATERIALS");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(Constants.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(lblTitle, gbc);

        JButton choose = new JButton("CHOOSE FILE (.PDF / .PPTX)");
        choose.setBackground(Constants.COLOR_SECONDARY);
        choose.setForeground(Color.WHITE);
        choose.setFont(new Font("SansSerif", Font.BOLD, 14));
        choose.setPreferredSize(new Dimension(250, 50));
        
        gbc.gridy = 1;
        card.add(choose, gbc);

        choose.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int r = fc.showOpenDialog(this);
            if(r == JFileChooser.APPROVE_OPTION){
                String path = fc.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(this, "Success: File linked to project.\nPath: " + path);
            }
        });

        add(card, BorderLayout.NORTH);
    }
}