public class StudentController {

    public void saveStudent(Student s) {
        FileHandler.write("students.txt", s.toFileString());
    }

    public Student loadStudent(String id) {
        // optional stub
        return null;
    }
}
