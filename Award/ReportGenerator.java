package shared.Award;

import shared.Constants;
import core.FileHandler;
import java.util.List;

public class ReportGenerator {

    public String generateGeneralReport() {
        StringBuilder report = new StringBuilder();
        DataAnalytics analytics = DataAnalytics.getInstance();

        report.append("========================================\n");
        report.append("       SEMINAR EVALUATION REPORT        \n");
        report.append("========================================\n\n");

        report.append("Highest Score Recorded: ").append(analytics.getHighestScore()).append("\n");
        report.append("------------------------------------------------------------\n");
        report.append(String.format("%-10s %-20s %-10s %-10s\n", "ID", "Name", "Type", "Score"));
        report.append("------------------------------------------------------------\n");

        List<String> lines = FileHandler.readAllLines(Constants.STUDENTS_FILE);

        for (String line : lines) {
            String[] data = line.split("\\" + Constants.DELIMITER);

            if (data.length >= 4) {
                String id = data[0].trim();
                String name = data[1].trim();
                String type = data[3].trim();
                double avg = analytics.calculateStudentAverage(id);

                if(avg > 0) {
                    report.append(String.format("%-10s %-20s %-10s %-10.2f\n", id, name, type, avg));
                }
            }
        }

        report.append("========================================\n");
        return report.toString();
    }
}