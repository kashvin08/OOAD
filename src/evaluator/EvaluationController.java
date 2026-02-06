package evaluator;


import shared.Constants;
import core.FileHandler;
import core.User;
import java.util.ArrayList;
import java.util.List;

public class EvaluationController {

    public ArrayList<String[]> getAssignedStudents(User evaluator) {
        ArrayList<String[]> list = new ArrayList<>();
        List<String> lines = FileHandler.readAllLines(Constants.SESSIONS_FILE);

        String myName = evaluator.getName();

        for (String line : lines) {
            String[] parts = line.split("\\" + Constants.DELIMITER);

            if (parts.length >= 6 && parts[4].trim().equalsIgnoreCase(myName)) {
                String studentListString = parts[5];
                String[] students = studentListString.split(",");

                for (String studentName : students) {
                    if (!studentName.trim().isEmpty()) {
                        String sessionInfo = "Session " + parts[0] + " (" + parts[3] + ")";
                        list.add(new String[]{studentName.trim(), sessionInfo});
                    }
                }
            }
        }
        return list;
    }

    public boolean isStudentEvaluated(String evalId, String studId) {
        return getEvaluationDetails(evalId, studId) != null;
    }

    public String[] getEvaluationDetails(String evalId, String studId) {
        List<String> lines = FileHandler.readAllLines(Constants.EVALUATIONS_FILE);
        String[] lastFound = null;

        for (String line : lines) {
            String[] parts = line.split("\\" + Constants.DELIMITER, -1);

            if (parts.length >= 8 && parts[0].equals(evalId) && parts[1].equals(studId)) {
                String rawComment = parts[7];
                String readableComment = rawComment.replace("<br>", "\n");

                lastFound = new String[]{
                    parts[2], parts[3], parts[4], parts[5],
                    parts[6],
                    readableComment
                };
            }
        }
        return lastFound;
    }

    public boolean saveEvaluation(String evalId, String studId, int[] scores, int total, String comment) {
        StringBuilder sb = new StringBuilder();
        sb.append(evalId).append(Constants.DELIMITER);
        sb.append(studId).append(Constants.DELIMITER);
        for (int s : scores) {
            sb.append(s).append(Constants.DELIMITER);
        }
        sb.append(total).append(Constants.DELIMITER);

        String safeComment;
        if (comment == null || comment.trim().isEmpty()) {
            safeComment = "-";
        } else {
            safeComment = comment.replace("\n", "<br>");
        }

        sb.append(safeComment);

        FileHandler.appendToFile(Constants.EVALUATIONS_FILE, sb.toString());
        return true;
    }
}
