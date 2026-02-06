package coordinator;

import java.util.List;

public class AssignmentSystem {
    //assign a student
    public boolean assignStudent(SessionManager.Session session, String studentName) {
        if (session == null || studentName == null || studentName.isEmpty()) {
            return false;
        }
        //prevent duplicates session
        if (session.assignedStudentNames.contains(studentName)) {
            System.out.println("Error: Student " + studentName + " is already in this session.");
            return false;
        }
        //max student
        if (session.assignedStudentNames.size() >= 10) {
            System.out.println("Error: Session is full (Max 10 students).");
            return false;
        }
        session.addStudent(studentName);
        SessionManager.getInstance().saveToFile();
        return true;
    }
    //valid student
    public boolean isStudentAlreadyAssigned(String studentName) {
        List<SessionManager.Session> allSessions = SessionManager.getInstance().getAllSessions();
        for (SessionManager.Session s : allSessions) {
            if (s.assignedStudentNames.contains(studentName)) {
                return true;
            }
        }
        return false;
    }
}

