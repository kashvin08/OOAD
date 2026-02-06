package award;

import shared.Constants;
import core.FileHandler;
import java.util.List;

public class ReportGenerator {

    public String generateGeneralReport() {
        StringBuilder report = new StringBuilder();
        DataAnalytics analytics = DataAnalytics.getInstance();

        report.append("============================================================\n");
        report.append("                FINAL SEMINAR SUMMARY             \n");
        report.append("============================================================\n\n");

        report.append(String.format("%-10s %-20s %-10s %-10s %-10s\n", "ID", "Name", "Type", "Score", "Status"));
        report.append("------------------------------------------------------------\n");

        List<String> submissionLines = FileHandler.readAllLines(Constants.SUBMISSIONS_FILE);

        for (String line : submissionLines) {
            String[] data = line.split("\\" + Constants.DELIMITER);

            if (data.length >= 4) {
                String id = data[0].trim();
                String name = data[1].trim();
                String type = data[3].trim(); // Presentation Type (Oral/Poster)
                double avg = analytics.calculateStudentAverage(id);

                String status = (avg > 0) ? "EVALUATED" : "PENDING";
                String scoreStr = (avg > 0) ? String.format("%.2f", avg) : "N/A";

                report.append(String.format("%-10s %-20s %-10s %-10s %-10s\n", 
                                             id, name, type, scoreStr, status));
            }
        }

        report.append("------------------------------------------------------------\n");
        report.append("Highest Score Recorded: ").append(analytics.getHighestScore()).append("\n");
        report.append("============================================================\n");
        return report.toString();
    }

    //oversee award nomination
    public String calculatePeoplesChoiceWinner() {
        java.io.File file = new java.io.File("data/votes.txt");
        if (!file.exists()) return "No votes found";

        java.util.Map<String, Integer> voteCount = new java.util.HashMap<>();
        List<String> lines = FileHandler.readAllLines("data/votes.txt");

        for (String line : lines) {
            String[] parts = line.split("\\" + Constants.DELIMITER);
            if (parts.length >= 1) {
                String studentID = parts[0].trim();
                voteCount.put(studentID, voteCount.getOrDefault(studentID, 0) + 1);
            }
        }

        return voteCount.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + " votes)")
                .orElse("None");
    }

    //get details
    public String getStudentCredentials(String targetID) {
        List<String> users = FileHandler.readAllLines(Constants.USERS_FILE);
        for (String line : users) {
            String[] parts = line.split("\\" + Constants.DELIMITER);
            if (parts[0].trim().equalsIgnoreCase(targetID)) {
                return "ID: " + parts[0] + "\nName: " + parts[1] + "\nEmail: " + parts[2] + "\nRole: " + parts[4];
            }
        }
        return "Record not found.";
    }
}
