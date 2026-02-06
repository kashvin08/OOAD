package core;

import shared.Constants;
import javax.swing.*;
import java.awt.*;
import core.User.UserRole;
import award.SystemAnalyticsFacade;

public class MainApplication extends JFrame {

    private JPanel currentPanel;
    private String currentUserID;
    private String currentUserName;
    private UserRole currentUserRole;


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

    private void buildDashboard() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = new JPanel(new BorderLayout());
        currentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        currentPanel.add(createNavigationPanel(), BorderLayout.WEST);
        currentPanel.add(createContentPanel(), BorderLayout.CENTER);

        add(currentPanel);
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

    private void buildRoleNavigation(JPanel navPanel) {
    switch (currentUserRole) {
        case STUDENT:
            addNavButton(navPanel, "Register Seminar");
            addNavButton(navPanel, "Upload Materials");
            addNavButton(navPanel, "View Schedule");
            break;

        case EVALUATOR:
            addNavButton(navPanel, "Evaluate Presentations");
            addNavButton(navPanel, "View Assignments");
            addNavButton(navPanel, "Submit Scores");
            break;

        case COORDINATOR:
            addNavButton(navPanel, "Create Session");
            addNavButton(navPanel, "Generate Reports");
            addNavButton(navPanel, "Manage Awards");
            break;
    }
}
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setPreferredSize(new Dimension(200, getHeight()));

        addNavButton(navPanel, "Dashboard");
        addNavButton(navPanel, "Profile");

        buildRoleNavigation(navPanel);

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

    private JPanel createAwardPanel() {
        SystemAnalyticsFacade facade = new SystemAnalyticsFacade();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JTextArea reportArea = new JTextArea(facade.generateFullReport());
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        String winners = "<html><b>Oral Winner:</b> " + facade.getOralWinner() + 
                         "<br><b>Poster Winner:</b> " + facade.getPosterWinner() + "</html>";
        JLabel lblWinners = new JLabel(winners);
        lblWinners.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton btnExport = new JButton("Export Report (TXT)");
        btnExport.addActionListener(e -> facade.exportReportToText("Seminar_Report_2026"));

        panel.add(lblWinners, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        panel.add(btnExport, BorderLayout.SOUTH);
        return panel;
    }

    private void handleNavigation(String menuItem) {
    // 1. Identify and remove the existing center component
    BorderLayout layout = (BorderLayout) currentPanel.getLayout();
    Component centerComp = layout.getLayoutComponent(BorderLayout.CENTER);
    
    if (centerComp != null) {
        currentPanel.remove(centerComp);
    }

    JPanel newContent;

    //specialized routing logic
    switch (menuItem) {
        case "Register Seminar":
            newContent = new student.RegistrationForm();
            break;
            
        case "Upload Materials":
            newContent = new student.UploadForm();
            break;

        case "Create Session":
    if (currentUserRole == UserRole.COORDINATOR) {
        newContent = new coordinator.CoordinatorGUI();
    } else {
        newContent = new JPanel();
        newContent.add(new JLabel("Access denied."));
    }
    break;

        case "View Schedule":
    if (currentUserRole == UserRole.STUDENT) {
        newContent = new student.StudentGUI(currentUserID, currentUserRole);
    } else if (currentUserRole == UserRole.COORDINATOR) {
        newContent = new coordinator.CoordinatorGUI();
    } else {
        newContent = new JPanel();
        newContent.add(new JLabel("Access denied."));
    }
    break;

        case "Evaluate Presentations":
        case "Submit Scores":
            evaluator.Evaluator evalUser = new evaluator.Evaluator(currentUserID, currentUserName, "eval@uni.edu", "nopass");
            newContent = new evaluator.EvaluatorGUI(evalUser); 
            break;

        case "View Assignments":
            evaluator.Evaluator viewOnlyEval = new evaluator.Evaluator(currentUserID, currentUserName, "eval@uni.edu", "nopass");
            evaluator.EvaluatorGUI viewPanel = new evaluator.EvaluatorGUI(viewOnlyEval);
            newContent = viewPanel;
            break;

        case "Manage Awards":
            // Create a master panel for the Coordinator to see everything
             JPanel masterAwardPanel = new JPanel(new BorderLayout());
    
            // Top Section: The Winners
            award.SystemAnalyticsFacade facade = new award.SystemAnalyticsFacade();
            String winnerInfo = "<html><div style='padding:10px; background:#FFE0B2;'>" +
                        "<b>🏆 Current Best Oral:</b> " + facade.getOralWinner() + " | " +
                        "<b>🏆 Current Best Poster:</b> " + facade.getPosterWinner() + "</div></html>";
            masterAwardPanel.add(new JLabel(winnerInfo), BorderLayout.NORTH);

            // Center Section: The Full Table (Detailed Credentials & Scores)
            JTextArea detailArea = new JTextArea(facade.generateFullReport());
            detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            detailArea.setEditable(false);
            masterAwardPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);

            newContent = masterAwardPanel;
         break;

        case "Generate Reports":
            //show ONLY the technical report (no winner headers)
            newContent = createReportPanel();
            break;

        case "Profile":
            newContent = createProfilePanel();
            break;

        case "Dashboard":
            newContent = createContentPanel();
            break;

        default:
            newContent = new JPanel();
            newContent.add(new JLabel("Module: " + menuItem + " coming soon."));
    }

    //re-inject and refresh the UI
    currentPanel.add(newContent, BorderLayout.CENTER);
    currentPanel.revalidate();
    currentPanel.repaint();
}

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginSystem(this).show();
        }
    }
    
    // Logic for a separate Report Panel (Technical View)
    private JPanel createReportPanel() {
        award.SystemAnalyticsFacade facade = new award.SystemAnalyticsFacade();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Full Technical Report"));
    
        JTextArea reportArea = new JTextArea(facade.generateFullReport());
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false);
        
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        return panel;
}

// Logic for the Profile Panel
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
    
        JLabel lblIcon = new JLabel("👤"); // Simple user icon
        lblIcon.setFont(new Font("Serif", Font.PLAIN, 100));
    
        JLabel lblName = new JLabel("Name: " + currentUserName);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 18));
    
        JLabel lblID = new JLabel("User ID: " + currentUserID);
        JLabel lblRole = new JLabel("Role: " + currentUserRole);
    
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblIcon, gbc);
        gbc.gridy = 1;
        panel.add(lblName, gbc);
        gbc.gridy = 2;
        panel.add(lblID, gbc);
        gbc.gridy = 3;
        panel.add(lblRole, gbc);
    
        return panel;
}

    public String getCurrentUserID() { return currentUserID; }
    public String getCurrentUserName() { return currentUserName; }
    public UserRole getCurrentUserRole() { return currentUserRole; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApplication::new);
    }
}