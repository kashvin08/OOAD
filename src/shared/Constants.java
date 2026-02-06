package shared;

import java.awt.Color;

public class Constants {//constants file for synchronizing code/methods/paths/etc. between our groupmates
    
    // ==================== FILE PATHS ====================
    public static final String DATA_DIR = "data/";
    public static final String USERS_FILE = DATA_DIR + "users.txt";
    public static final String STUDENTS_FILE = DATA_DIR + "students.txt";
    public static final String EVALUATORS_FILE = DATA_DIR + "evaluators.txt";
    public static final String SESSIONS_FILE = DATA_DIR + "sessions.txt";
    public static final String SUBMISSIONS_FILE = DATA_DIR + "submissions.txt";
    public static final String EVALUATIONS_FILE = DATA_DIR + "evaluations.txt";
    
    // ==================== DELIMITERS ====================
    public static final String DELIMITER = "|";
    public static final String NEWLINE = System.lineSeparator();
    
    // ==================== USER ROLES ====================
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_EVALUATOR = "EVALUATOR";
    public static final String ROLE_COORDINATOR = "COORDINATOR";
    
    // ==================== GUI CONSTANTS ====================
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;
    public static final String APP_TITLE = "FCI Seminar Management System";
    
    // Colors
    public static final Color PRIMARY_COLOR = new Color(0, 51, 102);    // Dark blue
    public static final Color COLOR_SECONDARY = new Color(0, 102, 204); // Blue
    public static final Color COLOR_ACCENT = new Color(255, 153, 0);    // Orange
    public static final Color COLOR_BACKGROUND = new Color(240, 245, 250);
    
    // ==================== PRESENTATION TYPES & CRITERIA ====================
    public static final String PRESENTATION_ORAL = "Oral";
    public static final String PRESENTATION_POSTER = "Poster";
    public static final String[] CRITERIA_ORAL = {"Delivery", "Content", "Slides", "Q&A"};
    public static final String[] CRITERIA_POSTER = {"Visual Layout", "Content Clarity", "Engagement", "Technical Depth"};
    
    // ==================== ERROR MESSAGES ====================
    public static final String ERROR_LOGIN_FAILED = "Invalid credentials!";
    public static final String ERROR_REQUIRED_FIELD = "This field is required!";
    
    // ==================== SUCCESS MESSAGES ====================
    public static final String SUCCESS_LOGIN = "Login successful!";
    public static final String SUCCESS_SAVE = "Data saved successfully!";
}