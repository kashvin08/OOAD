package core;

import shared.Constants;
import javax.swing.JPanel;

public abstract class User {

    protected final String userID;
    protected String name;
    protected String email;
    private String passwordHash;
    protected boolean isLoggedIn;
    protected UserRole role;

    public enum UserRole {
        STUDENT,
        EVALUATOR,
        COORDINATOR
    }

    public User(String userID, String name, String email, String password, UserRole role) {//const
        if (userID == null || userID.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.userID = userID.trim();
        this.name = name.trim();
        this.email = email.trim();
        this.passwordHash = core.FileHandler.hashPassword(password);
        this.role = role;
        this.isLoggedIn = false;
    }

    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public boolean isLoggedIn() { return isLoggedIn; }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) this.name = name;
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@")) this.email = email;
    }

    public boolean authenticate(String inputPassword) {
        String hashedInput = core.FileHandler.hashPassword(inputPassword);
        return passwordHash.equals(hashedInput);
    }

    public boolean login(String inputPassword) {
        if (authenticate(inputPassword)) {
            isLoggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() { isLoggedIn = false; }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (!authenticate(oldPassword)) return false;
        passwordHash = core.FileHandler.hashPassword(newPassword);
        return true;
    }

    public abstract String getDashboardTitle();
    public abstract JPanel getDashboardPanel();

    public void saveToFile() {
        String record = String.join(Constants.DELIMITER,
            userID,
            name,
            email,
            passwordHash,
            role.name()
        );
        core.FileHandler.appendToFile(Constants.USERS_FILE, record);
    }

    @Override
    public String toString() {
        return String.format("User[ID=%s, Name=%s, Role=%s]", userID, name, role);
    }
}
