package student;

import core.FileHandler;

public class StudentController {

    public void saveStudent(Student s) {
    FileHandler.appendToFile("students.txt", s.toFileString());
}

    public Student loadStudent(String id) {
        return null;
    }
}