package core;

import shared.Constants;
import javax.swing.*;
import java.awt.*;

//this is result form GUI (completed)
//read only GUI
public class ResultForm extends JFrame {

    public ResultForm(String studId, String title, String[] data) {
        setTitle("Evaluation Results: " + studId);
        setSize(500, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.COLOR_BACKGROUND);

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(Constants.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel l1 = new JLabel("Student: " + studId);
        JLabel l2 = new JLabel("Project: " + title);

        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        l1.setFont(headerFont);
        l2.setFont(headerFont);
        l1.setForeground(Color.WHITE);
        l2.setForeground(Color.WHITE);

        header.add(l1);
        header.add(l2);
        add(header, BorderLayout.NORTH);

        // Score Summary
        JPanel center = new JPanel(new GridLayout(6, 1, 10, 10));
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //each score
        addResultRow(center, RubricSystem.CRITERIA[0], data[0]);
        addResultRow(center, RubricSystem.CRITERIA[1], data[1]);
        addResultRow(center, RubricSystem.CRITERIA[2], data[2]);
        addResultRow(center, RubricSystem.CRITERIA[3], data[3]);

        //total score
        JLabel lblTotal = new JLabel("Total Score: " + data[4] + " / 20");
        lblTotal.setFont(new Font("Times New Roman", Font.BOLD, 22));
        lblTotal.setForeground(Constants.COLOR_ACCENT);
        lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(lblTotal);

        add(center, BorderLayout.CENTER);

        // Comment Section
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Constants.COLOR_BACKGROUND);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblCommentTitle = new JLabel("Evaluator Feedback:");
        lblCommentTitle.setFont(new Font("Times New Roman", Font.BOLD, 16));
        lblCommentTitle.setForeground(Constants.PRIMARY_COLOR);
        lblCommentTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Spacing below title

        // Text Area Setup
        JTextArea txtComment = new JTextArea();
        txtComment.setText(data[5]);
        txtComment.setEditable(false);
        txtComment.setLineWrap(true);
        txtComment.setWrapStyleWord(true);
        txtComment.setFont(new Font("SansSerif", Font.ITALIC, 14));
        txtComment.setBackground(Color.WHITE);

        txtComment.setFocusable(false); // Prevents the blinking "|" cursor
        txtComment.setHighlighter(null); // Prevents text selection highlighting

        // Add a border around the text
        txtComment.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside the box
        ));

        // Scroll Pane (in case comment is long)
        JScrollPane scrollPane = new JScrollPane(txtComment);
        scrollPane.setBorder(null); // Remove double border from scrollpane

        JButton btnClose = new JButton("Close Results");
        btnClose.setBackground(Constants.COLOR_SECONDARY);
        btnClose.setForeground(Color.WHITE);
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
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(label);
    }
}
