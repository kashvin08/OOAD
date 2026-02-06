package evaluator;

import shared.Constants;
import javax.swing.*;
import java.awt.*;

public class ResultForm extends JFrame {

    public ResultForm(String studId, String title, String[] data) {

        setTitle("Assessment Result - " + studId);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.COLOR_BACKGROUND);

        JPanel header = new JPanel(new GridLayout(3, 1));
        header.setBackground(Constants.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblMainTitle = new JLabel("OFFICIAL RESULT SUMMARY");
        lblMainTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblMainTitle.setForeground(Constants.COLOR_ACCENT);
        
        JLabel l1 = new JLabel("Student: " + studId);
        JLabel l2 = new JLabel("Project: " + title);
        
        Font headerFont = new Font("SansSerif", Font.PLAIN, 18);
        l1.setFont(headerFont); l2.setFont(headerFont);
        l1.setForeground(Color.WHITE); l2.setForeground(Color.WHITE);
        
        header.add(lblMainTitle);
        header.add(l1); 
        header.add(l2);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(6, 1, 15, 15)); 
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 50, 30, 50),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        
        addResultRow(center, RubricSystem.CRITERIA[0], data[0]);
        addResultRow(center, RubricSystem.CRITERIA[1], data[1]);
        addResultRow(center, RubricSystem.CRITERIA[2], data[2]);
        addResultRow(center, RubricSystem.CRITERIA[3], data[3]);
        
        JLabel lblTotal = new JLabel("Total Score: " + data[4] + " / 20");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 30)); // Bigger Total
        lblTotal.setForeground(Constants.COLOR_ACCENT);
        lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(lblTotal);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Constants.COLOR_BACKGROUND);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblCommentTitle = new JLabel("Evaluator Feedback:");
        lblCommentTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblCommentTitle.setForeground(Constants.PRIMARY_COLOR);
        lblCommentTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JTextArea txtComment = new JTextArea();
        txtComment.setText(data[5]); 
        txtComment.setEditable(false);
        txtComment.setLineWrap(true);
        txtComment.setWrapStyleWord(true);
        txtComment.setFont(new Font("SansSerif", Font.ITALIC, 16));
        txtComment.setBackground(Color.WHITE); 
        txtComment.setFocusable(false); 
        txtComment.setHighlighter(null); 
        
        txtComment.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JScrollPane scrollPane = new JScrollPane(txtComment);
        scrollPane.setBorder(null);
        
        JButton btnClose = new JButton("Close Results");
        btnClose.setBackground(Constants.COLOR_SECONDARY);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnClose.setPreferredSize(new Dimension(150, 40));
        btnClose.addActionListener(e -> dispose());
        
        bottom.add(lblCommentTitle, BorderLayout.NORTH);
        bottom.add(scrollPane, BorderLayout.CENTER);
        bottom.add(btnClose, BorderLayout.SOUTH);
        
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addResultRow(JPanel panel, String criteria, String scoreStr) {
        int score = Integer.parseInt(scoreStr);
        String ratingText = RubricSystem.RATINGS[score - 1]; 
        JLabel label = new JLabel(criteria + ": " + ratingText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Bigger text
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // Indent
        panel.add(label);
    }
}