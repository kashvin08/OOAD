package core;

import shared.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import evaluator.Evaluator;

public class MainApplication extends JFrame {

    private JPanel currentPanel;
    private String currentUserID;
    private String currentUserName;
    private UserRole currentUserRole;

    public enum UserRole {//overriding for different dashboards (role based)
        STUDENT {
            @Override
            void buildNavigation(MainApplication app, JPanel nav) {
                app.addNavButton(nav, "Register Seminar");
                app.addNavButton(nav, "Upload Materials");
                app.addNavButton(nav, "View Schedule");
                app.addNavButton(nav, "Vote for Presentation");
            }
        },
        EVALUATOR {
            @Override
            void buildNavigation(MainApplication app, JPanel nav) {
                app.addNavButton(nav, "Evaluate Presentations");
                app.addNavButton(nav, "View Assignments");
            }
        },
        COORDINATOR {
            @Override
            void buildNavigation(MainApplication app, JPanel nav) {
                app.addNavButton(nav, "Create Session");
                app.addNavButton(nav, "Generate Reports");
                app.addNavButton(nav, "Manage Awards");
            }
        };

        abstract void buildNavigation(MainApplication app, JPanel nav);
    }

    public MainApplication() {
        initialiseUI();
        new LoginSystem(this);
    }

    private void initialiseUI() {
        setTitle(Constants.APP_TITLE);
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    public void showDashboard(String userID, String userName, UserRole role) {
        this.currentUserID = userID;
        this.currentUserName = userName;
        this.currentUserRole = role;

        setVisible(true);
        buildDashboard();
    }

    private void buildDashboard() {//diffrent dashboards for different roles
        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = new JPanel(new BorderLayout());
        currentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        currentPanel.add(createNavigationPanel(), BorderLayout.WEST);

        if (currentUserRole == UserRole.EVALUATOR) {
            Evaluator eval = new Evaluator(currentUserID, currentUserName, "notset@system.com", "evaa");
            currentPanel.add(eval.getDashboardPanel(), BorderLayout.CENTER);
        } else if (currentUserRole == UserRole.STUDENT) {
            student.Student stu = new student.Student(currentUserID, currentUserName, "notset@system.com", "evi");
            currentPanel.add(stu.getDashboardPanel(), BorderLayout.CENTER);
        } else if (currentUserRole == UserRole.COORDINATOR) {
            coordinator.Coordinator coord = new coordinator.Coordinator(currentUserID, currentUserName, "coord@system.com", "N/A");
            currentPanel.add(coord.getDashboardPanel(), BorderLayout.CENTER);
        }

        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Constants.PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel title = new JLabel(Constants.APP_TITLE, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> logout());

        header.add(title, BorderLayout.CENTER);
        header.add(logout, BorderLayout.EAST);

        return header;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setPreferredSize(new Dimension(200, getHeight()));

        addNavButton(navPanel, "Dashboard");
        addNavButton(navPanel, "Profile");

        currentUserRole.buildNavigation(this, navPanel);

        navPanel.add(Box.createVerticalGlue());
        return navPanel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel(
                "Welcome, " + currentUserName + " (" + currentUserRole + ")",
                SwingConstants.CENTER
        );
        welcome.setFont(new Font("Arial", Font.BOLD, 24));
        welcome.setForeground(Constants.PRIMARY_COLOR);

        panel.add(welcome, BorderLayout.CENTER);
        return panel;
    }

    private void addNavButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);

        button.addActionListener(e -> handleNavigation(text));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(button);
    }

    private void handleNavigation(String menuItem) {//navigations for each role
        if (menuItem.equals("Dashboard")) {
            buildDashboard();
            return;
        }

        if (menuItem.equals("Profile")) {
            JOptionPane.showMessageDialog(this, "User: " + currentUserName + "\nID: " + currentUserID + "\nRole: " + currentUserRole);
            return;
        }

        if (currentUserRole == UserRole.STUDENT) {
            switch (menuItem) {
                case "Register Seminar":
                    SwingUtilities.invokeLater(() -> new student.RegistrationForm(currentUserID, currentUserName));
                    return;
                case "Upload Materials":
                    SwingUtilities.invokeLater(() -> new student.UploadForm(currentUserID, currentUserName));
                    return;
                case "View Schedule":
                    showScheduleView();
                    return;
                case "Vote for Presentation": 
                    SwingUtilities.invokeLater(() -> new student.VotingForm(currentUserID));
                    return;
                }
        } 
        
        else if (currentUserRole == UserRole.EVALUATOR) {
            Component center = ((BorderLayout) currentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (center instanceof evaluator.EvaluatorGUI) {
                evaluator.EvaluatorGUI evalGUI = (evaluator.EvaluatorGUI) center;
                switch (menuItem) {
                    case "Evaluate Presentations":
                        evalGUI.handleInteraction();
                        return;
                    case "View Assignments":
                        evalGUI.loadData();
                        JOptionPane.showMessageDialog(this, "Assignments Refreshed!");
                        return;
                }
            }
        } 
        
        else if (currentUserRole == UserRole.COORDINATOR) {
            Component center = ((BorderLayout) currentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (center instanceof coordinator.CoordinatorGUI) {
                coordinator.CoordinatorGUI coordGUI = (coordinator.CoordinatorGUI) center;
                switch (menuItem) {
                    case "Create Session":
                        coordGUI.refreshTable();
                        return;
                    case "Generate Reports":
                        coordGUI.showProgressReport();
                        return;
                    case "Manage Awards":
                        coordGUI.showAwardNominations();
                        return;
                }
            }
        }
    } 

    private void showScheduleView() {//schedule viewing
        JFrame frame = new JFrame("My Seminar Schedule");
        frame.setSize(950, 300); 
        frame.setLocationRelativeTo(this);

        String[] columns = {"ID", "Name", "Title", "Type", "Time", "Board ID", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        List<String> subLines = FileHandler.readAllLines(shared.Constants.SUBMISSIONS_FILE);
        List<String> sessionLines = FileHandler.readAllLines(shared.Constants.SESSIONS_FILE);

        for (String subLine : subLines) {
            String[] subData = subLine.split("\\" + shared.Constants.DELIMITER);
            
            if (subData.length >= 8 && subData[0].equals(currentUserID)) {
                String studentName = subData[1];
                String sessionTime = "Not Assigned";

                for (String sLine : sessionLines) {
                    String[] sParts = sLine.split("\\" + Constants.DELIMITER);
                    if (sParts.length >= 6 && sParts[5].contains(studentName)) {
                        sessionTime = sParts[2]; 
                        break;
                    }
                }

                String boardID = (subData.length >= 9) ? subData[8] : "N/A";

                Object[] tableRow = {
                    subData[0],//id
                    subData[1],//name
                    subData[2],//title
                    subData[4],//type
                    sessionTime,//time
                    boardID,//board id (in pdf reqs)
                    subData[7]//status
                };
                model.addRow(tableRow);
            }
        }

        JTable table = new JTable(model);
        frame.add(new JScrollPane(table));

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No seminars registered yet!");
        } else {
            frame.setVisible(true);
        }
    }

    private void logout() {//logout
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginSystem(this).show();
        }
    }

    public String getCurrentUserID() { return currentUserID; }
    public String getCurrentUserName() { return currentUserName; }
    public UserRole getCurrentUserRole() { return currentUserRole; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApplication::new);
    }
}