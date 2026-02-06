package award;

import shared.Constants;
import core.FileHandler;
import java.util.List;

public class AwardSystem {

    public AwardSystem() {}

    public class ReportGenerator {

    public String generateGeneralReport() {
        StringBuilder report = new StringBuilder();
        DataAnalytics analytics = DataAnalytics.getInstance();

        report.append("============================================================\n");
        report.append("                SEMINAR EVALUATION REPORT                   \n");
        report.append("============================================================\n\n");

        report.append(String.format("%-10s %-15s %-15s %-10s\n", "ID", "Name", "Type", "Avg Score"));
        report.append("------------------------------------------------------------\n");

        // Load SUBMISSIONS to get the presentation type (Oral/Poster)
        List<String> lines = FileHandler.readAllLines(Constants.SUBMISSIONS_FILE);

        for (String line : lines) {
            String[] data = line.split("\\" + Constants.DELIMITER);

            if (data.length >= 4) {
                String id = data[0].trim();
                String name = data[1].trim();
                String type = data[3].trim(); // ORAL or POSTER
                
                // Get average from analytics
                double avg = analytics.calculateStudentAverage(id);

                // Only show if evaluated, otherwise show "Pending"
                String scoreStr = (avg > 0) ? String.format("%.2f", avg) : "Pending";
                report.append(String.format("%-10s %-15s %-15s %-10s\n", id, name, type, scoreStr));
            }
        }

        report.append("------------------------------------------------------------\n");
        report.append("Highest Score Recorded: ").append(String.format("%.2f", analytics.getHighestScore())).append("\n");
        report.append("============================================================\n");
        
        return report.toString();
    }
}
    public String determineWinner(String category) {
        String winnerID = "None";
        String winnerName = "No Candidate";
        double highestAvg = -1.0;

        DataAnalytics analytics = DataAnalytics.getInstance();
        List<String> lines = FileHandler.readAllLines(Constants.STUDENTS_FILE);

        for (String line : lines) {
            // NOTE: Ensure Student.java uses Constants.DELIMITER ("|") not ","
            String[] data = line.split("\\" + Constants.DELIMITER);

            // Expected: ID|Name|Title|Type|FilePath
            if (data.length >= 4) {
                String type = data[3].trim();

                if (type.equalsIgnoreCase(category)) {
                    String id = data[0].trim();
                    double avg = analytics.calculateStudentAverage(id);

                    if (avg > highestAvg) {
                        highestAvg = avg;
                        winnerID = id;
                        winnerName = data[1].trim();
                    }
                }
            }
        }

        if (highestAvg <= 0) return "No evaluations found for " + category;
        return winnerName + " (" + String.format("%.2f", highestAvg) + ")";
    }
}
