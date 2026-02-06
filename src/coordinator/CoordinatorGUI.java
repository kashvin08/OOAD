package coordinator;

import shared.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import core.FileHandler;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Constants.PRIMARY_COLOR);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnCreate = createStyledButton("Create Session");
        JButton btnEdit = createStyledButton("Edit");
        JButton btnDelete = createStyledButton("Delete");
        JButton btnManual = createStyledButton("Assign Student");
        JButton btnGenerate = createStyledButton("Print to PDF"); 
        JButton btnRefresh = createStyledButton("Refresh");

        controlPanel.add(btnCreate);
        controlPanel.add(btnEdit);
        controlPanel.add(btnDelete);
        controlPanel.add(btnManual);
        controlPanel.add(btnGenerate);
        controlPanel.add(btnRefresh);
        add(controlPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Date", "Time", "Venue", "Evaluator", "Students"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        sessionTable = new JTable(tableModel);
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionTable.setRowHeight(25); 
        add(new JScrollPane(sessionTable), BorderLayout.CENTER);

        refreshTable();

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
            int confirm = JOptionPane.showConfirmDialog(this, "Delete Session ID: " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                sessionManager.deleteSession(id);
                refreshTable();
            }
        });

        btnManual.addActionListener(e -> showManualAssignDialog());
        btnGenerate.addActionListener(e -> printToPDF());
        btnRefresh.addActionListener(e -> refreshTable());
    }

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

        try {
            MessageFormat header = new MessageFormat("Seminar Presentation Schedule");
            MessageFormat footer = new MessageFormat("Page {0,number,integer}");
            sessionTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showCreateSessionDialog() {//create session section
        JPanel datePickerPanel = createDatePickerPanel(null);
        String[] timeOptions = {"08:00 - 10:00", "10:00 - 12:00", "12:00 - 14:00", "14:00 - 16:00", "16:00 - 18:00"};
        String[] venueOptions = {"D01 (Lab 1)", "D02 (Lab 2)", "Auditorium A", "Meeting Room 1", "Online (Teams)"};
        List<String> evaluatorList = loadEvaluatorNames();
        String[] evaluatorOptions = evaluatorList.toArray(new String[0]);

        JComboBox<String> timeBox = new JComboBox<>(timeOptions);
        JComboBox<String> venueBox = new JComboBox<>(venueOptions);
        JComboBox<String> evaluatorBox = new JComboBox<>(evaluatorOptions);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("Select Date:")); inputPanel.add(datePickerPanel);
        inputPanel.add(new JLabel("Select Time:")); inputPanel.add(timeBox);
        inputPanel.add(new JLabel("Select Venue:")); inputPanel.add(venueBox);
        inputPanel.add(new JLabel("Select Evaluator:")); inputPanel.add(evaluatorBox);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Create New Session", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String dateString = getSelectedDateString();
            if (!isDateValid(dateString)) {
                JOptionPane.showMessageDialog(this, "Error: You cannot schedule a session in the past!","Invalid Date", JOptionPane.ERROR_MESSAGE); 
                return; 
            }
            sessionManager.createSession(getSelectedDateString(), (String)timeBox.getSelectedItem(), (String)venueBox.getSelectedItem(), (String)evaluatorBox.getSelectedItem());
            refreshTable();
        }
    }

    private void showEditSessionDialog(int id, String currentDate, String currentTime, String currentVenue, String currentEval) {//edit session
        JPanel datePickerPanel = createDatePickerPanel(currentDate);
        String[] timeOptions = {"08:00 - 10:00", "10:00 - 12:00", "12:00 - 14:00", "14:00 - 16:00", "16:00 - 18:00"};
        String[] venueOptions = {"D01 (Lab 1)", "D02 (Lab 2)", "Auditorium A", "Meeting Room 1", "Online (Teams)"};
        List<String> evaluatorList = loadEvaluatorNames();
        JComboBox<String> timeBox = new JComboBox<>(timeOptions);
        JComboBox<String> venueBox = new JComboBox<>(venueOptions);
        JComboBox<String> evaluatorBox = new JComboBox<>(evaluatorList.toArray(new String[0]));

        timeBox.setSelectedItem(currentTime);
        venueBox.setSelectedItem(currentVenue);
        evaluatorBox.setSelectedItem(currentEval);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.add(new JLabel("Session ID:")); inputPanel.add(new JLabel(String.valueOf(id)));
        inputPanel.add(new JLabel("New Date:")); inputPanel.add(datePickerPanel);
        inputPanel.add(new JLabel("New Time:")); inputPanel.add(timeBox);
        inputPanel.add(new JLabel("New Venue:")); inputPanel.add(venueBox);
        inputPanel.add(new JLabel("New Evaluator:")); inputPanel.add(evaluatorBox);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Session", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            sessionManager.updateSession(id, getSelectedDateString(), (String)timeBox.getSelectedItem(), (String)venueBox.getSelectedItem(), (String)evaluatorBox.getSelectedItem());
            refreshTable();
        }
    }

    public void showManualAssignDialog() {
        List<String> studentNames = loadStudentNames();
        if (studentNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students registered yet!");
            return;
        }
        List<SessionManager.Session> sessions = sessionManager.getAllSessions();
        if (sessions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sessions available!");
            return;
        }
        String[] sessionOptions = new String[sessions.size()];
        for (int i = 0; i < sessions.size(); i++) {
            SessionManager.Session s = sessions.get(i);
            sessionOptions[i] = "ID: " + s.id + " | " + s.date;
        }

        JComboBox<String> sessionBox = new JComboBox<>(sessionOptions);
        JComboBox<String> studentBox = new JComboBox<>(studentNames.toArray(new String[0]));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("Select Session:")); inputPanel.add(sessionBox);
        inputPanel.add(new JLabel("Select Student:")); inputPanel.add(studentBox);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Assign Student", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            SessionManager.Session target = sessions.get(sessionBox.getSelectedIndex());
            String student = (String) studentBox.getSelectedItem();
            if (assignmentSystem.assignStudent(target, student)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Assigned!");
            }
        }
    }
//dating
    private JPanel createDatePickerPanel(String defaultDate) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        yearBox = new JComboBox<>();
        for (int i = 2026; i <= 2030; i++) yearBox.addItem(i);
        monthBox = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        dayBox = new JComboBox<>();
        updateDayBox();
        yearBox.addActionListener(e -> updateDayBox());
        monthBox.addActionListener(e -> updateDayBox());
        if (defaultDate != null) {
            String[] parts = defaultDate.split("-");
            yearBox.setSelectedItem(Integer.parseInt(parts[0]));
            monthBox.setSelectedItem(parts[1]);
            dayBox.setSelectedItem(Integer.parseInt(parts[2]));
        }
        panel.add(yearBox); panel.add(new JLabel("-"));
        panel.add(monthBox); panel.add(new JLabel("-"));
        panel.add(dayBox);
        return panel;
    }

    private void updateDayBox() {
        int year = (Integer) yearBox.getSelectedItem();
        int month = monthBox.getSelectedIndex() + 1;
        int days = (month == 2) ? ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0) ? 29 : 28) : (month == 4 || month == 6 || month == 9 || month == 11) ? 30 : 31;
        dayBox.removeAllItems();
        for (int i = 1; i <= days; i++) dayBox.addItem(i);
    }

    private String getSelectedDateString() {
        int d = (Integer) dayBox.getSelectedItem();
        return yearBox.getSelectedItem() + "-" + monthBox.getSelectedItem() + "-" + (d < 10 ? "0" + d : d);
    }
    
    private boolean isDateValid(String selectedDate) {
        try {
            java.time.LocalDate selected = java.time.LocalDate.parse(selectedDate);
            java.time.LocalDate today = java.time.LocalDate.now();
            return !selected.isBefore(today);
        } catch (Exception e) {
            return false;
        }
    }
//load evaluator name from .txt
    private List<String> loadEvaluatorNames() {
        List<String> names = new ArrayList<>();
        List<String> lines = core.FileHandler.readAllLines(Constants.USERS_FILE);
        for (String line : lines) {
            String[] parts = line.split("\\" + Constants.DELIMITER);
            if (parts.length >= 5 && parts[4].equalsIgnoreCase("EVALUATOR")) names.add(parts[1]);
        }
        return names;
    }
//load student name from .txt
    private List<String> loadStudentNames() {
        List<String> names = new ArrayList<>();
        List<String> lines = core.FileHandler.readAllLines(Constants.SUBMISSIONS_FILE);
        for (String line : lines) {
            String[] parts = line.split("\\" + Constants.DELIMITER);
            if (parts.length >= 2) names.add(parts[1]);
        }
        return names;
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (SessionManager.Session s : sessionManager.getAllSessions()) {
            tableModel.addRow(new Object[]{ s.id, s.date, s.time, s.venue, s.evaluatorName, String.join(", ", s.assignedStudentNames) });
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Constants.PRIMARY_COLOR);
        btn.setFocusPainted(false);
        return btn;
    }
//people choice award
    private String calculatePeoplesChoice() {
        List<String> votes = core.FileHandler.readAllLines("votes.txt");
        if (votes.isEmpty()) return "No votes cast yet";

        Map<String, Integer> tallyMap = new HashMap<>();
        for (String line : votes) {
            String[] parts = line.split("\\" + shared.Constants.DELIMITER);
            if (parts.length >= 2) {
                String nominee = parts[1];
                tallyMap.put(nominee, tallyMap.getOrDefault(nominee, 0) + 1);
            }
        }

        int maxVotes = 0;
        List<String> winners = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : tallyMap.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winners.clear();
                winners.add(entry.getKey());
            } else if (entry.getValue() == maxVotes) {
                winners.add(entry.getKey());
            }
        }

        if (winners.isEmpty()) return "No valid votes found";
        
        if (winners.size() > 1) {
            return "TIE! (" + String.join(" & ", winners) + ") with " + maxVotes + " votes";
        }
        return winners.get(0) + " (" + maxVotes + " votes)";
    }
//award popup
    public void showAwardNominations() {
        award.SystemAnalyticsFacade facade = new award.SystemAnalyticsFacade();
        
        String oralWinner = facade.getOralWinner();
        String posterWinner = facade.getPosterWinner();
        String peoplesChoice = calculatePeoplesChoice();
        
        StringBuilder message = new StringBuilder();
        message.append("🏆 SEMINAR AWARD NOMINATIONS 🏆\n");
        message.append("------------------------------------------\n");
        message.append("🥇 BEST ORAL: ").append(oralWinner).append("\n");
        message.append("🥇 BEST POSTER: ").append(posterWinner).append("\n");
        message.append("⭐ PEOPLE'S CHOICE: ").append(peoplesChoice).append("\n");
        message.append("------------------------------------------\n");
        message.append("Would you like to export the FULL student list\n");
        message.append("to 'Seminar_final_awards.txt'?");

        int choice = JOptionPane.showConfirmDialog(this, message.toString(), 
                     "Award Ceremony", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            facade.exportReportToText("Seminar_final_awards.txt");
        }
    }

    public void showProgressReport() {
        List<String> submissions = core.FileHandler.readAllLines(Constants.SUBMISSIONS_FILE);
        int total = submissions.size();
        int graded = 0;
        for (String line : submissions) if (line.contains("GRADED")) graded++;

        String report = String.format("Seminar Progress Report\nTotal: %d\nGraded: %d\nPending: %d", total, graded, total - graded);
        JOptionPane.showMessageDialog(this, report, "System Analytics", JOptionPane.INFORMATION_MESSAGE);
    }
}