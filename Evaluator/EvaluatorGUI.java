package core;

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

        // Header
        JLabel title = new JLabel("Assigned Presentations");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Constants.PRIMARY_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Table
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

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Constants.COLOR_BACKGROUND);

        JButton btnAction = new JButton("Evaluate / View");
        JButton btnRefresh = new JButton("Refresh");

        btnAction.setBackground(Constants.COLOR_ACCENT);
        btnAction.setForeground(Color.WHITE);

        btnAction.addActionListener(e -> handleInteraction());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnRefresh);
        btnPanel.add(btnAction);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);

        ArrayList<String[]> data = controller.getAssignedStudents(evaluator);

        for (String[] row : data) {

            boolean isDone = controller.isStudentEvaluated(evaluator.getUserID(), row[0]);
            model.addRow(new Object[]{row[0], row[1], isDone ? "Completed" : "Pending"});
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
