package evaluator;

import shared.Constants;
import core.User;
import core.FileHandler;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationController {

    public ArrayList<String[]> getAssignedStudents(User evaluator) {//get assigned students from .txt
        ArrayList<String[]> list = new ArrayList<>();
        List<String> sessionLines = FileHandler.readAllLines(Constants.SESSIONS_FILE);
        List<String> submissionLines = FileHandler.readAllLines(Constants.SUBMISSIONS_FILE);

        String myName = evaluator.getName();

        for (String sLine : sessionLines) {
            String[] sParts = sLine.split("\\" + Constants.DELIMITER);
            if (sParts.length >= 6 && sParts[4].trim().equalsIgnoreCase(myName)) {
                String[] studentNamesInSession = sParts[5].split(",");

                for (String sName : studentNamesInSession) {
                    String studID = "UNKNOWN", title = "No Title", presentationType = "Oral", abstr = "No Abstract", path = "No File";
                    
                    for (String subLine : submissionLines) {
                        String[] subParts = subLine.split("\\" + Constants.DELIMITER);
                        if (subParts.length >= 7 && subParts[1].trim().equalsIgnoreCase(sName.trim())) {
                            studID = subParts[0]; 
                            title = subParts[2];
                            presentationType = subParts[4];
                            abstr = subParts[5];
                            path = subParts[6];
                            break;
                        }
                    }
                    
                    String sessionInfo = "Session " + sParts[0] + " (" + sParts[3] + ")";
                    list.add(new String[]{studID, sName.trim(), sessionInfo, presentationType, title, abstr, path});
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

    private void updateStudentStatus(String studId, String newStatus) {
        List<String> lines = FileHandler.readAllLines(Constants.SUBMISSIONS_FILE);
        boolean updated = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split("\\" + Constants.DELIMITER);
            if (parts.length >= 8 && (parts[0].equals(studId) || parts[1].equals(studId))) {
                parts[7] = newStatus; 
                lines.set(i, String.join(Constants.DELIMITER, parts));
                updated = true;
                break;
            }
        }

        if (updated) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(Constants.SUBMISSIONS_FILE))) {
                for (String line : lines) {
                    pw.println(line);
                }
            } catch (IOException e) {
                System.err.println("Error updating status: " + e.getMessage());
            }
        }
    }

    public boolean saveEvaluation(String evalId, String studId, int[] scores, int total, String comment) {
        StringBuilder sb = new StringBuilder();
        sb.append(evalId).append(Constants.DELIMITER);
        sb.append(studId).append(Constants.DELIMITER);
        for (int s : scores) {
            sb.append(s).append(Constants.DELIMITER);
        }
        sb.append(total).append(Constants.DELIMITER);

        String safeComment = (comment == null || comment.trim().isEmpty()) ? "-" : comment.replace("\n", "<br>");
        sb.append(safeComment);

        FileHandler.appendToFile(Constants.EVALUATIONS_FILE, sb.toString());//append comment to .txt
        updateStudentStatus(studId, "GRADED (" + total + "/20)");
        
        return true;
    }
}