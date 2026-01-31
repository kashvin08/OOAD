package core;

import shared.Constants;
import javax.swing.*;
import java.awt.*;

//this is evaluate form GUI
//new evaluate and reevaluate both use same
public class EvaluationForm extends JFrame {

    private JComboBox<String>[] dropdowns;
    private JTextArea txtComment;
    private EvaluationController controller;
    private RubricSystem rubric;
    private String evalId, studId;

    public EvaluationForm(String evalId, String studId, String title, EvaluationController controller) {
        this.evalId = evalId;
        this.studId = studId;
        this.controller = controller;
        this.rubric = new RubricSystem();

        setTitle("Grading: " + studId);
        setSize(600, 700);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.COLOR_BACKGROUND);

        // Info
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(Constants.PRIMARY_COLOR);

        JLabel l1 = new JLabel("  Student: " + studId);
        JLabel l2 = new JLabel("  Topic: " + title);

        Font f = new Font("SansSerif", Font.BOLD, 16);
        l1.setFont(f);
        l2.setFont(f);
        l1.setForeground(Color.WHITE);
        l2.setForeground(Color.WHITE);

        info.add(l1);
        info.add(l2);
        add(info, BorderLayout.NORTH);

        // Rubrics
        JPanel center = new JPanel(new GridLayout(5, 2, 10, 10));
        center.setBackground(Constants.COLOR_BACKGROUND);
        center.setBorder(BorderFactory.createTitledBorder("Rubrics"));

        //rating 
        dropdowns = new JComboBox[4];
        for (int i = 0; i < 4; i++) {
            center.add(new JLabel(RubricSystem.CRITERIA[i]));
            dropdowns[i] = new JComboBox<>(RubricSystem.RATINGS);
            dropdowns[i].setBackground(Color.WHITE);
            center.add(dropdowns[i]);
        }
        add(center, BorderLayout.CENTER);

        // Comment & Submit
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Constants.COLOR_BACKGROUND);

        txtComment = new JTextArea(4, 20);
        txtComment.setBorder(BorderFactory.createTitledBorder("Feedback"));

        JButton btnSubmit = new JButton("Submit Evaluation");
        btnSubmit.setBackground(Constants.COLOR_ACCENT);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnSubmit.addActionListener(e -> submit());

        bottom.add(new JScrollPane(txtComment), BorderLayout.CENTER);
        bottom.add(btnSubmit, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Helper to pre-fill data for Re-evaluation
    public void fillData(String[] data) {
        // Data: {s1, s2, s3, s4, total, comment}
        for (int i = 0; i < 4; i++) {
            int score = Integer.parseInt(data[i]);
            //adjust index so index 0 is score 1 instead of score 0
            if (score >= 1 && score <= 5) {
                dropdowns[i].setSelectedIndex(score - 1);
            }
        }
        txtComment.setText(data[5]);
    }

    private void submit() {
        int[] scores = new int[4];
        for (int i = 0; i < 4; i++) {
            String val = (String) dropdowns[i].getSelectedItem();
            scores[i] = rubric.getScoreValue(val);
        }

        int total = rubric.calculateTotal(scores);
        String cmt = txtComment.getText().replace(Constants.DELIMITER, " ");

        boolean success = controller.saveEvaluation(evalId, studId, scores, total, cmt);

        if (success) {
            JOptionPane.showMessageDialog(this, Constants.SUCCESS_SAVE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error saving data.");
        }
    }
}
