package coordinator;


import shared.Constants;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SessionManager {
    private static SessionManager instance;
    private List<Session> sessions;
    private SessionManager() {
        sessions = new ArrayList<>();
        loadFromFile();
    }
    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }
    public void createSession(String date, String time, String venue, String evaluatorName) {
        //ID auto increment
        int nextId = sessions.isEmpty() ? 1 : sessions.get(sessions.size() - 1).id + 1;
        Session newSession = new Session(nextId, date, time, venue, evaluatorName);
        sessions.add(newSession);
        saveToFile();
    }

    //Update Sesion
    public void updateSession(int id, String newDate, String newTime, String newVenue, String newEvaluator) {
        for (Session s : sessions) {
            if (s.id == id) {
                s.date = newDate;
                s.time = newTime;
                s.venue = newVenue;
                s.evaluatorName = newEvaluator;
                break;
            }
        }
        saveToFile(); //txt
    }

    //Delete
    public void deleteSession(int id) {
        sessions.removeIf(s -> s.id == id);
        saveToFile(); // Save changes to TXT
    }
    public List<Session> getAllSessions() {
        return sessions;
    }
    public void saveToFile() {
        File file = new File(Constants.SESSIONS_FILE);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Session s : sessions) {
                StringBuilder sb = new StringBuilder();
                sb.append(s.id).append(Constants.DELIMITER)
                        .append(s.date).append(Constants.DELIMITER)
                        .append(s.time).append(Constants.DELIMITER)
                        .append(s.venue).append(Constants.DELIMITER)
                        .append(s.evaluatorName).append(Constants.DELIMITER);
                for (int i = 0; i < s.assignedStudentNames.size(); i++) {
                    sb.append(s.assignedStudentNames.get(i));
                    if (i < s.assignedStudentNames.size() - 1) sb.append(",");
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    public void loadFromFile() {
        sessions.clear();
        File file = new File(Constants.SESSIONS_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(Pattern.quote(Constants.DELIMITER));
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0]);
                    String date = parts[1];
                    String time = parts[2];
                    String venue = parts[3];
                    String evalName = parts[4];
                    Session s = new Session(id, date, time, venue, evalName);
                    if (parts.length > 5 && !parts[5].isEmpty()) {
                        String[] students = parts[5].split(",");
                        for (String name : students) s.addStudent(name);
                    }
                    sessions.add(s);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public class Session {
        public int id;
        public String date, time, venue, evaluatorName;
        public List<String> assignedStudentNames;
        public Session(int id, String date, String time, String venue, String evaluatorName) {
            this.id = id;
            this.date = date;
            this.time = time;
            this.venue = venue;
            this.evaluatorName = evaluatorName;
            this.assignedStudentNames = new ArrayList<>();
        }
        public void addStudent(String name) {
            assignedStudentNames.add(name);
        }
    }
}
