package evaluator;

import shared.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class EvaluatorGUI extends JPanel {
    private Evaluator evaluator;
    private EvaluationController controller;
    private JTable table;
    private DefaultTableModel model;

    public EvaluatorGUI(Evaluator evaluator) {
        this.evaluator = evaluator;
        this.controller = new EvaluationController();

        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_BACKGROUND);

        //header
        JLabel title = new JLabel("Assigned Presentations");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Constants.PRIMARY_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        //table
        String[] cols = {"Student Name", "Session Details", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(Constants.COLOR_SECONDARY);
        table.getTableHeader().setForeground(Color.WHITE);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleInteraction();
                }
            }
        });

        loadData();

        add(new JScrollPane(table), BorderLayout.CENTER);

        //buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Constants.COLOR_BACKGROUND);

        JButton btnAction = new JButton("Evaluate");
        JButton btnRefresh = new JButton("Refresh");

        btnAction.setBackground(Constants.COLOR_ACCENT);
        btnAction.setForeground(Color.WHITE);

        btnAction.addActionListener(e -> handleInteraction());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnRefresh);
        btnPanel.add(btnAction);
        add(btnPanel, BorderLayout.SOUTH);
        
        JButton btnViewDetails = new JButton("View Project Details");
        btnViewDetails.setBackground(shared.Constants.COLOR_SECONDARY);
        btnViewDetails.setForeground(Color.WHITE);
        
        btnViewDetails.addActionListener(e -> viewProjectDetailsAction());
        
        btnPanel.add(btnRefresh);
        btnPanel.add(btnViewDetails);
        btnPanel.add(btnAction);
    }

    private void loadData() {
        model.setRowCount(0);

        ArrayList<String[]> data = controller.getAssignedStudents(evaluator);

        for (String[] row : data) {

            boolean isDone = controller.isStudentEvaluated(evaluator.getUserID(), row[0]);
            model.addRow(new Object[]{row[0], row[1], isDone ? "Completed" : "Pending"});
        }
    }

    private String[] getProjectDetails(String studentName) {
        //read from submissions.txt
        java.util.List<String> lines = core.FileHandler.readAllLines(shared.Constants.SUBMISSIONS_FILE);
        for (String line : lines) {
            String[] parts = line.split("\\" + shared.Constants.DELIMITER);
            if (parts.length >= 3 && parts[1].trim().equalsIgnoreCase(studentName)) {
                return parts;
            }
        }
        return null;
    }
    
    private void showProjectInfo(String studentName) {
        String[] details = getProjectDetails(studentName);

        if (details == null) {
            JOptionPane.showMessageDialog(this, "No submission data found for " + studentName, 
                "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder html = new StringBuilder("<html><body style='width: 400px; padding: 10px;'>");
        html.append("<h2 style='color:#003366;'>").append(details[2]).append("</h2>"); // Title
        html.append("<hr>");
        html.append("<b>Presentation Type:</b> ").append(details[3]).append("<br><br>");
        html.append("<b>Abstract:</b><br><p style='text-align:justify;'>").append(details[4]).append("</p><br>");
        
        if (details.length > 5) {
            html.append("<div style='background:#eeeeee; padding:5px;'>");
            html.append("<b>File Path:</b> <code style='color:blue;'>").append(details[5]).append("</code>");
            html.append("</div>");
        }
        html.append("</body></html>");

        JLabel lblDetails = new JLabel(html.toString());
        JScrollPane scroll = new JScrollPane(lblDetails);
        scroll.setPreferredSize(new Dimension(450, 350));
        scroll.setBorder(null);

        JOptionPane.showMessageDialog(this, scroll, "Project Details: " + studentName, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void viewProjectDetailsAction() {
        int row = table.getSelectedRow();
        if (row != -1) {
            String sName = (String) model.getValueAt(row, 0);
            showProjectInfo(sName);
        } else {
            JOptionPane.showMessageDialog(this, "Select a student to view their abstract.");
        }
    }
    
    private void handleInteraction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

        String sName = (String) model.getValueAt(row, 0);
        String details = (String) model.getValueAt(row, 1);
        String status = (String) model.getValueAt(row, 2);

        if (status.equals("Completed")) {
            Object[] options = {"Re-evaluate", "View Results"};
            int choice = JOptionPane.showOptionDialog(this, "Choose action:", "Options",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

            if (choice == JOptionPane.YES_OPTION) {
                EvaluationForm form = new EvaluationForm(evaluator.getUserID(), sName, details, controller);
                String[] data = controller.getEvaluationDetails(evaluator.getUserID(), sName);
                if (data != null) {
                    form.fillData(data);
                }
                form.setVisible(true);
            } else if (choice == JOptionPane.NO_OPTION) {
                String[] data = controller.getEvaluationDetails(evaluator.getUserID(), sName);
                if (data != null) {
                    new ResultForm(sName, details, data);
                }
            }
        } else {
            EvaluationForm form = new EvaluationForm(evaluator.getUserID(), sName, details, controller);
            form.setVisible(true);
        }
    }
}
