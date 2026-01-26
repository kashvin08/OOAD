package core;

import shared.Constants;
import javax.swing.JPanel;

public abstract class User { //future proof [OCP]

    protected final String userID;
    protected String name;
    protected String email;
    private String passwordHash; //encapsulation[never directly accessible]
    protected boolean isLoggedIn;
    protected UserRole role;

    //enum for predefined role/constants [OCP]
    public enum UserRole {
        STUDENT,
        COORDINATOR,
        CUSTOMER
    }

    //constructor
    public User(String userID, String name, String email, String password, UserRole role) {

        if (userID == null || userID.trim().isEmpty()) { //validation prevents invalid objects from exisiting
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        this.userID = userID.trim();
        this.name = name.trim();
        this.email = email.trim();
        this.passwordHash = hash(password);
        this.role = role;
        this.isLoggedIn = false;
    }

    //getters
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public boolean isLoggedIn() { return isLoggedIn; }

    //setter with validation
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }
    //setter with validation
    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email;
        }
    }

    //consistent authentication for all classes(LSP)
    public boolean authenticate(String inputPassword) {
        return passwordHash.equals(hash(inputPassword));
    }

    //login/logout (LSP)
    public boolean login(String inputPassword) {
        if (authenticate(inputPassword)) {
            isLoggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() { //LSP
        isLoggedIn = false;
    }

    //change password
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!authenticate(oldPassword)) {
            return false;
        }
        passwordHash = hash(newPassword);
        return true;
    }

    public abstract String getDashboardTitle();
    public abstract JPanel getDashboardPanel(); //abstract ui (ocp+lsp)

    //persistent saving 
    public void saveToFile() {
        String record = String.join(Constants.DELIMITER,
            userID,
            name,
            email,
            passwordHash,
            role.name()
        );
        FileHandler.appendToFile(Constants.USERS_FILE, record);
    }

    //hash helper
    private String hash(String input) {
        return Integer.toString(input.hashCode());
    }

    @Override
    public String toString() { //overrides the default version into readable format
        return String.format(
            "User[ID=%s, Name=%s, Role=%s]",
            userID, name, role
        );
    }
}
