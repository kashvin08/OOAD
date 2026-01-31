public class Student extends User {

    private String title;
    private String abstractTxt;
    private String supervisor;
    private String presentationType;
    private String filePath;

    public Student(String id, String name, String password) {
        super(id, name, password, "Student");
    }

    public void register(String t, String abs, String sup, String type) {
        this.title = t;
        this.abstractTxt = abs;
        this.supervisor = sup;
        this.presentationType = type;
    }

    public void uploadFile(String path) {
        this.filePath = path;
    }

    public String toFileString() {
        return userID + "," + name + "," + title + "," +
               presentationType + "," + filePath;
    }
}
