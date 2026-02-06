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

        JLabel titleLabel = new JLabel("Assigned Presentations");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Constants.PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        String[] cols = {"ID", "Student Name", "Session Details", "Type", "Status", "Title", "Abstract", "File Path"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        
        //hide columns for cleaner look
        table.removeColumn(table.getColumnModel().getColumn(7)); //path
        table.removeColumn(table.getColumnModel().getColumn(6)); //abstract
        table.removeColumn(table.getColumnModel().getColumn(5)); //title
        table.removeColumn(table.getColumnModel().getColumn(0)); //id

        table.setRowHeight(30);
        table.getTableHeader().setBackground(Constants.COLOR_SECONDARY);
        table.getTableHeader().setForeground(Color.WHITE);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleInteraction();
                }
            }
        });

        loadData();
        add(new JScrollPane(table), BorderLayout.CENTER);

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

    public void loadData() {
        model.setRowCount(0);
        ArrayList<String[]> data = controller.getAssignedStudents(evaluator);

        for (String[] row : data) {
            boolean isDone = controller.isStudentEvaluated(evaluator.getUserID(), row[0]);
            
            model.addRow(new Object[]{
                row[0],//id
                row[1],//name
                row[2],//session details
                row[3],//type (oral/poster)
                isDone ? "Completed" : "Pending",
                row[4],//title
                row[5],//abstract
                row[6]//path
            });
        }
    }

    public void handleInteraction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

//indices must match model struct
        String sId = (String) model.getValueAt(row, 0);   
        String sName = (String) model.getValueAt(row, 1); 
        String presentationType = (String) model.getValueAt(row, 3);
        String status = (String) model.getValueAt(row, 4);
        String projectTitle = (String) model.getValueAt(row, 5);
        String projectAbstract = (String) model.getValueAt(row, 6);
        String filePath = (String) model.getValueAt(row, 7);

        String submissionInfo = "Type: " + presentationType +
                                "\nProject: " + projectTitle + 
                                "\n\nAbstract: " + projectAbstract + 
                                "\n\nFile Path: " + filePath;

        if (status.equals("Completed")) {
            Object[] options = {"Re-evaluate", "View Results"};
            int choice = JOptionPane.showOptionDialog(this, submissionInfo + "\n\nChoose action:", "Submission Review",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

            if (choice == JOptionPane.YES_OPTION) {
                EvaluationForm form = new EvaluationForm(evaluator.getUserID(), sId, projectTitle, presentationType, controller);
                String[] data = controller.getEvaluationDetails(evaluator.getUserID(), sId);
                if (data != null) {
                    form.fillData(data);
                }
                form.setVisible(true);
            } else if (choice == JOptionPane.NO_OPTION) {
                String[] data = controller.getEvaluationDetails(evaluator.getUserID(), sId);
                if (data != null) {
                    new ResultForm(sName, projectTitle, data);
                }
            }
        } else {
            int openFile = JOptionPane.showConfirmDialog(this, submissionInfo + "\n\nDo you want to open materials and start grading?", 
                                                       "Start Evaluation", JOptionPane.YES_NO_OPTION);
            
            if (openFile == JOptionPane.YES_OPTION) {
                try {
                    java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Note: File could not be opened automatically.");
                }
                
                EvaluationForm form = new EvaluationForm(evaluator.getUserID(), sId, projectTitle, presentationType, controller);
                form.setVisible(true);
            }
        }
    }
}