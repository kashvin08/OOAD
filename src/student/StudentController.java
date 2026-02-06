package student;
import core.FileHandler;
import shared.Constants;

public class StudentController {

    public void saveStudent(Student s) {
        String record = s.toFileString();
        FileHandler.appendToFile(Constants.SUBMISSIONS_FILE, record);
        
        System.out.println("Successfully wrote to " + Constants.SUBMISSIONS_FILE);
    }

    public Student loadStudent(String id) {
        // optional stub
        return null;
    }
}
