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

    public EvaluationForm(String evalId, String studId, String title, EvaluationController controller) {
        this.evalId = evalId;
        this.studId = studId;
        this.controller = controller;
        this.rubric = new RubricSystem();

        setTitle("Grading Form - Student: " + studId);
        setTitle("Evaluation Form");

        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.COLOR_BACKGROUND);

        JPanel info = new JPanel(new GridLayout(3, 1)); 
        info.setBackground(Constants.PRIMARY_COLOR);
        info.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblMainTitle = new JLabel("EVALUATION FORM"); 
        lblMainTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblMainTitle.setForeground(Constants.COLOR_ACCENT);
        
        JLabel l1 = new JLabel("Student: " + studId);
        JLabel l2 = new JLabel("Project: " + title);
        
        Font f = new Font("SansSerif", Font.PLAIN, 18);
        l1.setFont(f); l2.setFont(f);
        l1.setForeground(Color.WHITE); l2.setForeground(Color.WHITE);
        
        info.add(lblMainTitle);
        info.add(l1); 
        info.add(l2);
        add(info, BorderLayout.NORTH);

        //rubrics
        JPanel center = new JPanel(new GridLayout(5, 2, 20, 20)); 
        center.setBackground(Constants.COLOR_BACKGROUND);
        center.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.PRIMARY_COLOR), 
            "Scoring Criteria",
            0, 0, new Font("SansSerif", Font.BOLD, 16)
        ));

        dropdowns = new JComboBox[4];
        for(int i=0; i<4; i++) {
            JLabel lblCriteria = new JLabel(RubricSystem.CRITERIA[i]);
            lblCriteria.setFont(new Font("SansSerif", Font.BOLD, 16));
            center.add(lblCriteria);
            
            dropdowns[i] = new JComboBox<>(RubricSystem.RATINGS);
            dropdowns[i].setFont(new Font("SansSerif", Font.PLAIN, 14));
            dropdowns[i].setBackground(Color.WHITE);
            center.add(dropdowns[i]);
        }

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(Constants.COLOR_BACKGROUND);
        centerContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerContainer.add(center, BorderLayout.CENTER);
        
        add(centerContainer, BorderLayout.CENTER);

        //comment
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Constants.COLOR_BACKGROUND);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        txtComment = new JTextArea(4, 20);
        txtComment.setBorder(BorderFactory.createTitledBorder("Additional Feedback"));
        txtComment.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JButton btnSubmit = new JButton("Submit Evaluation");
        btnSubmit.setBackground(Constants.COLOR_ACCENT);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnSubmit.setPreferredSize(new Dimension(200, 50)); 
        
        btnSubmit.addActionListener(e -> submit());
        
        bottom.add(new JScrollPane(txtComment), BorderLayout.CENTER);
        bottom.add(btnSubmit, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    public void fillData(String[] data) {
        for(int i=0; i<4; i++) {
            int score = Integer.parseInt(data[i]);
            if(score >= 1 && score <= 5) {
                dropdowns[i].setSelectedIndex(score - 1);
            }
        }
        txtComment.setText(data[5]);
    }

    private void submit() {
        int[] scores = new int[4];
        for(int i=0; i<4; i++) {
            String val = (String) dropdowns[i].getSelectedItem();
            scores[i] = rubric.getScoreValue(val);
        }
        
        int total = rubric.calculateTotal(scores);
        String cmt = txtComment.getText();
        
        boolean success = controller.saveEvaluation(evalId, studId, scores, total, cmt);
        
        if(success) {
            JOptionPane.showMessageDialog(this, Constants.SUCCESS_SAVE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error saving data.");
        }
    }
}