package evaluator;

import shared.Constants;
import javax.swing.*;
import java.awt.*;

public class EvaluationForm extends JFrame {

    private JComboBox<String>[] dropdowns;
    private JTextArea txtComment;
    private EvaluationController controller;
    private RubricSystem rubric;
    private String evalId, studId;

    public EvaluationForm(String evalId, String studId, String title, String presentationType, EvaluationController controller) {
        this.evalId = evalId;
        this.studId = studId;
        this.controller = controller;
        this.rubric = new RubricSystem();

        boolean isPoster = presentationType.equalsIgnoreCase("Poster");

        setTitle("Grading: " + studId + (isPoster ? " [POSTER]" : " [ORAL]"));
        setSize(600, 750);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.COLOR_BACKGROUND);

        //info panel
        JPanel info = new JPanel(new GridLayout(isPoster ? 3 : 2, 1));
        info.setBackground(Constants.PRIMARY_COLOR);

        JLabel l1 = new JLabel("  Student: " + studId);
        JLabel l2 = new JLabel("  Topic: " + title);
        
        Font f = new Font("SansSerif", Font.BOLD, 16);
        l1.setFont(f); l2.setFont(f);
        l1.setForeground(Color.WHITE); l2.setForeground(Color.WHITE);

        info.add(l1);
        info.add(l2);

        if (isPoster) {
            JLabel l3 = new JLabel("  Category: Poster Presentation (Visual Criteria Applied)");
            l3.setFont(new Font("SansSerif", Font.ITALIC, 14));
            l3.setForeground(Color.YELLOW);
            info.add(l3);
        }

        add(info, BorderLayout.NORTH);

        //rubrics panel
        JPanel center = new JPanel(new GridLayout(5, 2, 10, 10));
        center.setBackground(Constants.COLOR_BACKGROUND);
        center.setBorder(BorderFactory.createTitledBorder("Evaluation Criteria"));

        String[] criteriaLabels;//criteria based on pres types
        if (isPoster) {
            criteriaLabels = new String[]{"Visual Layout", "Content Clarity", "Engagement", "Technical Depth"};
        } else {
            criteriaLabels = new String[]{"Oral Delivery", "Slide Quality", "Q&A Handling", "Time Management"};
        }

        dropdowns = new JComboBox[4];
        for (int i = 0; i < 4; i++) {
            JLabel critLabel = new JLabel(criteriaLabels[i]);
            critLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            center.add(critLabel);
            
            dropdowns[i] = new JComboBox<>(RubricSystem.RATINGS);
            dropdowns[i].setBackground(Color.WHITE);
            center.add(dropdowns[i]);
        }
        add(center, BorderLayout.CENTER);

        //comment & submit
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Constants.COLOR_BACKGROUND);

        txtComment = new JTextArea(4, 20);
        txtComment.setBorder(BorderFactory.createTitledBorder("Evaluator Feedback"));

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

    public void fillData(String[] data) {
        for (int i = 0; i < 4; i++) {
            try {
                int score = Integer.parseInt(data[i]);
                if (score >= 1 && score <= 5) {
                    dropdowns[i].setSelectedIndex(score - 1);
                }
            } catch (Exception e) { }
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