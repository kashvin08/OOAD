package core;

import shared.Constants;
import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {

    private JPanel currentPanel;
    private String currentUserID;
    private String currentUserName;
    private UserRole currentUserRole;

    public enum UserRole {//role enum OSP,LSP

        STUDENT {
            @Override
            void buildNavigation(MainApplication app, JPanel nav) {
                app.addNavButton(nav, "Register Seminar");
                app.addNavButton(nav, "Upload Materials");
                app.addNavButton(nav, "View Schedule");
            }
        },

        EVALUATOR {
            @Override
            void buildNavigation(MainApplication app, JPanel nav) {
                app.addNavButton(nav, "Evaluate Presentations");
                app.addNavButton(nav, "View Assignments");
                app.addNavButton(nav, "Submit Scores");
            }
        },

        COORDINATOR {
            @Override
            void buildNavigation(MainApplication app, JPanel nav) {
                app.addNavButton(nav, "Create Session");
                app.addNavButton(nav, "Assign Roles");
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

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setPreferredSize(new Dimension(200, getHeight()));

        addNavButton(navPanel, "Dashboard");//common navigation
        addNavButton(navPanel, "Profile");

        currentUserRole.buildNavigation(this, navPanel);//role based navigation

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

    private void addNavButton(JPanel panel, String text) {//shared navigation
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);

        button.addActionListener(e -> handleNavigation(text));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(button);
    }

    private void handleNavigation(String menuItem) {
        JOptionPane.showMessageDialog(
            this,
            menuItem + " module will be loaded here.",
            "Navigation",
            JOptionPane.INFORMATION_MESSAGE
        );
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

    public String getCurrentUserID() { return currentUserID; }//getters
    public String getCurrentUserName() { return currentUserName; }
    public UserRole getCurrentUserRole() { return currentUserRole; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApplication::new);
    }
}
