package student;

import shared.Constants;
import core.User.UserRole;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.MessageFormat;

public class StudentGUI extends JPanel {
    private JTable scheduleTable;
    private DefaultTableModel model;
    private String studentID;

    public StudentGUI(String studentID, UserRole role) {
        this.studentID = studentID;
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.COLOR_BACKGROUND);

        // 1. Header
        JLabel lblTitle = new JLabel("My Seminar Schedule", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Schedule Table (View Only)
        String[] cols = {"Date", "Time", "Venue", "Evaluator", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; } // Strict view-only
        };
        scheduleTable = new JTable(model);
        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        // 3. Control Panel (Only View/Download)
        JPanel btnPanel = new JPanel();
        JButton btnDownload = new JButton("Download Schedule (PDF)");
        JButton btnRefresh = new JButton("Refresh");

        btnDownload.addActionListener(e -> downloadPDF());
        btnRefresh.addActionListener(e -> loadScheduleData());

        btnPanel.add(btnRefresh);
        btnPanel.add(btnDownload);
        add(btnPanel, BorderLayout.SOUTH);

        loadScheduleData();
    }

    private void loadScheduleData() {
        model.setRowCount(0);
        java.util.List<String> sessions = core.FileHandler.readAllLines(Constants.SESSIONS_FILE);
        for (String line : sessions) {
            String[] p = line.split("\\" + Constants.DELIMITER);
            if (p.length >= 6 && p[5].contains(studentID)) {
                model.addRow(new Object[]{p[1], p[2], p[3], p[4], "Confirmed"});
            }
        }
    }

    private void downloadPDF() {
        try {
            MessageFormat header = new MessageFormat("Personal Seminar Schedule - " + studentID);
            scheduleTable.print(JTable.PrintMode.FIT_WIDTH, header, new MessageFormat("Page {0}"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating PDF: " + ex.getMessage());
        }
    }
}