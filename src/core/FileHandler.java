package core;

import shared.Constants;
import java.io.*;
import java.util.*;

public class FileHandler {

    static {
        new File(Constants.DATA_DIR).mkdirs();//ensure data dir exists
    }
//hashing password for better security
    public static String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }

    public static void appendToFile(String filename, String record) {//writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(record);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to " + filename + ": " + e.getMessage());
        }
    }

    public static List<String> readAllLines(String filename) {//reading
        List<String> lines = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + filename + ": " + e.getMessage());
        }
        return lines;
    }
    
    public static void updateSubmissionFile(String studentID, String filePath) {
    List<String> lines = readAllLines(Constants.SUBMISSIONS_FILE);
    boolean found = false;

    for (int i = 0; i < lines.size(); i++) {
        String[] parts = lines.get(i).split("\\" + Constants.DELIMITER);
        if (parts[0].equals(studentID)) {
            parts[6] = filePath; 
            lines.set(i, String.join(Constants.DELIMITER, parts));
            found = true;
            break;
        }
    }

    if (found) {
        //overwrite file with updated list
        try (PrintWriter pw = new PrintWriter(new FileWriter(Constants.SUBMISSIONS_FILE))) {
            for (String line : lines) {
                pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }
}
    
    public static boolean userExists(String userID) {
        return findUserRecord(userID) != null;
    }

    private static String findUserRecord(String userID) {
        for (String record : readAllLines(Constants.USERS_FILE)) {
            String[] parts = parse(record);
            if (parts != null && parts[0].equals(userID)) {
                return record;
            }
        }
        return null;
    }

    public static String[] authenticate(String userID, String password) {//authenticate

        String record = findUserRecord(userID);
        if (record == null) return null;

        String[] parts = parse(record);
        if (parts == null) return null;

        String hashedInput = hashPassword(password);//hash input password before comparing

        if (!parts[3].equals(hashedInput)) return null;

        return parts;
    }

    private static String[] parse(String record) {//parsing
        String[] parts = record.split("\\" + Constants.DELIMITER);
        return parts.length >= 5 ? parts : null;
    }
}