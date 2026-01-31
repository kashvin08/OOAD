package modules.coordinator;

import shared.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CoordinatorGUI extends JPanel {
    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private SessionManager sessionManager;
    private ScheduleGenerator scheduleGenerator;
    private AssignmentSystem assignmentSystem;
    private JComboBox<Integer> yearBox, dayBox;
    private JComboBox<String> monthBox;

    public CoordinatorGUI() {
        sessionManager = SessionManager.getInstance();
        scheduleGenerator = new ScheduleGenerator();
        assignmentSystem = new AssignmentSystem();

        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_BACKGROUND);
        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Constants.PRIMARY_COLOR);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnCreate = createStyledButton("Create Session");
        JButton btnEdit = createStyledButton("Edit");
        JButton btnDelete = createStyledButton("Delete");
        JButton btnManual = createStyledButton("Assign Student");
        JButton btnGenerate = createStyledButton("Print to PDF"); // Changed to Print
        JButton btnRefresh = createStyledButton("Refresh");

        controlPanel.add(btnCreate);
        controlPanel.add(btnEdit);
        controlPanel.add(btnDelete);
        controlPanel.add(btnManual);
        controlPanel.add(btnGenerate);
        controlPanel.add(btnRefresh);
        add(controlPanel, BorderLayout.NORTH);

        //Interface
        String[] columnNames = {"ID", "Date", "Time", "Venue", "Evaluator", "Students"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sessionTable = new JTable(tableModel);
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionTable.setRowHeight(25); // Increase row height for better printing
        add(new JScrollPane(sessionTable), BorderLayout.CENTER);

        refreshTable();

        //Button
        btnCreate.addActionListener(e -> showCreateSessionDialog());

        btnEdit.addActionListener(e -> {
            int selectedRow = sessionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a session to edit.");
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String date = (String) tableModel.getValueAt(selectedRow, 1);
            String time = (String) tableModel.getValueAt(selectedRow, 2);
            String venue = (String) tableModel.getValueAt(selectedRow, 3);
            String eval = (String) tableModel.getValueAt(selectedRow, 4);
            showEditSessionDialog(id, date, time, venue, eval);
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = sessionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a session to delete.");
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete Session ID: " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                sessionManager.deleteSession(id);
                refreshTable();
            }
        });

        btnManual.addActionListener(e -> showManualAssignDialog());

        //call gen sort and print
        btnGenerate.addActionListener(e -> printToPDF());

        btnRefresh.addActionListener(e -> refreshTable());
    }

    //gen pdf
    private void printToPDF() {
        List<SessionManager.Session> orderedSessions = scheduleGenerator.getOrderedSchedule();
        if (orderedSessions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No schedule to print!");
            return;
        }
        tableModel.setRowCount(0);
        for (SessionManager.Session s : orderedSessions) {
            String students = String.join(", ", s.assignedStudentNames);
            Object[] rowData = { s.id, s.date, s.time, s.venue, s.evaluatorName, students };
            tableModel.addRow(rowData);
        }

        //call sys print
        try {
            MessageFormat header = new MessageFormat("Seminar Presentation Schedule");
            MessageFormat footer = new MessageFormat("Page {0,number,integer}");

            boolean complete = sessionTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);

            if (complete) {
                JOptionPane.showMessageDialog(this, "PDF Saved Successfully!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Manage/Assign
    private void showCreateSessionDialog() {
        JPanel datePickerPanel = createDatePickerPanel(null);

        String[] timeOptions = {"08:00 - 10:00", "10:00 - 12:00", "12:00 - 14:00", "14:00 - 16:00", "16:00 - 18:00"};
        String[] venueOptions = {"D01 (Lab 1)", "D02 (Lab 2)", "Auditorium A", "Meeting Room 1", "Online (Teams)"};

        List<String> evaluatorList = loadEvaluatorNames();
        if (evaluatorList.isEmpty()) evaluatorList.add("No Evaluators Found");
        String[] evaluatorOptions = evaluatorList.toArray(new String[0]);

        JComboBox<String> timeBox = new JComboBox<>(timeOptions);
        JComboBox<String> venueBox = new JComboBox<>(venueOptions);
        JComboBox<String> evaluatorBox = new JComboBox<>(evaluatorOptions);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("Select Date:"));
        inputPanel.add(datePickerPanel);
        inputPanel.add(new JLabel("Select Time:"));
        inputPanel.add(timeBox);
        inputPanel.add(new JLabel("Select Venue:"));
        inputPanel.add(venueBox);
        inputPanel.add(new JLabel("Select Evaluator:"));
        inputPanel.add(evaluatorBox);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Create New Session", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String dateString = getSelectedDateString();
            String time = (String) timeBox.getSelectedItem();
            String venue = (String) venueBox.getSelectedItem();
            String evaluator = (String) evaluatorBox.getSelectedItem();

            if (evaluator.equals("No Evaluators Found")) return;

            sessionManager.createSession(dateString, time, venue, evaluator);
            refreshTable();
            JOptionPane.showMessageDialog(this, Constants.SUCCESS_SAVE);
        }
    }

    private void showEditSessionDialog(int id, String currentDate, String currentTime, String currentVenue, String currentEval) {
        JPanel datePickerPanel = createDatePickerPanel(currentDate);

        String[] timeOptions = {"08:00 - 10:00", "10:00 - 12:00", "12:00 - 14:00", "14:00 - 16:00", "16:00 - 18:00"};
        String[] venueOptions = {"D01 (Lab 1)", "D02 (Lab 2)", "Auditorium A", "Meeting Room 1", "Online (Teams)"};
        List<String> evaluatorList = loadEvaluatorNames();
        String[] evaluatorOptions = evaluatorList.toArray(new String[0]);

        JComboBox<String> timeBox = new JComboBox<>(timeOptions);
        JComboBox<String> venueBox = new JComboBox<>(venueOptions);
        JComboBox<String> evaluatorBox = new JComboBox<>(evaluatorOptions);

        timeBox.setSelectedItem(currentTime);
        venueBox.setSelectedItem(currentVenue);
        evaluatorBox.setSelectedItem(currentEval);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.add(new JLabel("Session ID:"));
        inputPanel.add(new JLabel(String.valueOf(id)));
        inputPanel.add(new JLabel("New Date:"));
        inputPanel.add(datePickerPanel);
        inputPanel.add(new JLabel("New Time:"));
        inputPanel.add(timeBox);
        inputPanel.add(new JLabel("New Venue:"));
        inputPanel.add(venueBox);
        inputPanel.add(new JLabel("New Evaluator:"));
        inputPanel.add(evaluatorBox);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Session " + id, JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String newDate = getSelectedDateString();
            String newTime = (String) timeBox.getSelectedItem();
            String newVenue = (String) venueBox.getSelectedItem();
            String newEval = (String) evaluatorBox.getSelectedItem();

            sessionManager.updateSession(id, newDate, newTime, newVenue, newEval);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Session Updated!");
        }
    }

    private void showManualAssignDialog() {
        List<SessionManager.Session> sessions = sessionManager.getAllSessions();
        if (sessions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sessions available!");
            return;
        }
        String[] sessionOptions = new String[sessions.size()];
        for (int i = 0; i < sessions.size(); i++) {
            SessionManager.Session s = sessions.get(i);
            sessionOptions[i] = "ID: " + s.id + " | " + s.date + " (" + s.time + ")";
        }

        List<String> students = loadStudentNames();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students found in students.txt!");
            return;
        }
        String[] studentOptions = students.toArray(new String[0]);

        JComboBox<String> sessionBox = new JComboBox<>(sessionOptions);
        JComboBox<String> studentBox = new JComboBox<>(studentOptions);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("Select Session:"));
        inputPanel.add(sessionBox);
        inputPanel.add(new JLabel("Select Student:"));
        inputPanel.add(studentBox);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Assign Student", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int selectedIndex = sessionBox.getSelectedIndex();
            SessionManager.Session targetSession = sessions.get(selectedIndex);
            String selectedStudent = (String) studentBox.getSelectedItem();

            boolean success = assignmentSystem.assignStudent(targetSession, selectedStudent);
            if (success) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Assigned: " + selectedStudent);
            } else {
                JOptionPane.showMessageDialog(this, "Failed! Duplicate or Full.");
            }
        }
    }

    //Date
    private JPanel createDatePickerPanel(String defaultDate) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        yearBox = new JComboBox<>();
        for (int i = 2025; i <= 2030; i++) yearBox.addItem(i);
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        monthBox = new JComboBox<>(months);
        dayBox = new JComboBox<>();
        updateDayBox();
        yearBox.addActionListener(e -> updateDayBox());
        monthBox.addActionListener(e -> updateDayBox());
        if (defaultDate != null) {
            try {
                String[] parts = defaultDate.split("-");
                yearBox.setSelectedItem(Integer.parseInt(parts[0]));
                monthBox.setSelectedItem(parts[1]);
                dayBox.setSelectedItem(Integer.parseInt(parts[2]));
            } catch (Exception e) {}
        }
        panel.add(yearBox);
        panel.add(new JLabel("-"));
        panel.add(monthBox);
        panel.add(new JLabel("-"));
        panel.add(dayBox);
        return panel;
    }

    private void updateDayBox() {
        Integer currentSelection = (Integer) dayBox.getSelectedItem();
        int year = (Integer) yearBox.getSelectedItem();
        int month = monthBox.getSelectedIndex() + 1;
        int daysInMonth = (month == 2) ? ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0) ? 29 : 28) : (month == 4 || month == 6 || month == 9 || month == 11) ? 30 : 31;
        dayBox.removeAllItems();
        for (int i = 1; i <= daysInMonth; i++) dayBox.addItem(i);
        if (currentSelection != null) dayBox.setSelectedItem(currentSelection > daysInMonth ? daysInMonth : currentSelection);
    }

    private String getSelectedDateString() {
        int d = (Integer) dayBox.getSelectedItem();
        return yearBox.getSelectedItem() + "-" + monthBox.getSelectedItem() + "-" + (d < 10 ? "0" + d : d);
    }

    private List<String> loadEvaluatorNames() {
        List<String> names = new ArrayList<>();
        File file = new File(Constants.EVALUATORS_FILE);
        if (!file.exists()) return names;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(Pattern.quote(Constants.DELIMITER));
                if (parts.length >= 2) names.add(parts[1]);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return names;
    }

    private List<String> loadStudentNames() {
        List<String> names = new ArrayList<>();
        File file = new File(Constants.STUDENTS_FILE);
        if (!file.exists()) return names;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(Pattern.quote(Constants.DELIMITER));
                if (parts.length >= 2) names.add(parts[1]);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return names;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<SessionManager.Session> sessions = sessionManager.getAllSessions();
        for (SessionManager.Session s : sessions) {
            String students = String.join(", ", s.assignedStudentNames);
            Object[] rowData = { s.id, s.date, s.time, s.venue, s.evaluatorName, students };
            tableModel.addRow(rowData);
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Constants.PRIMARY_COLOR);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        return btn;
    }
}