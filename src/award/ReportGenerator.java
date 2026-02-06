package award;

import shared.Constants;
import java.util.List;

public class ReportGenerator {

    public String generateGeneralReport() {
    StringBuilder report = new StringBuilder();
    DataAnalytics analytics = DataAnalytics.getInstance();

    report.append("============================================================\n");
    report.append("               FINAL SEMINAR SUBMISSIONS & SCORES           \n");
    report.append("============================================================\n");
    report.append(String.format("%-8s | %-15s | %-10s | %-10s\n", "ID", "Name", "Type", "Score"));
    report.append("------------------------------------------------------------\n");

    List<String> subLines = core.FileHandler.readAllLines(shared.Constants.SUBMISSIONS_FILE);

    for (String line : subLines) {
        String[] data = line.split("\\" + shared.Constants.DELIMITER);
        if (data.length >= 5) {
            String id = data[0].trim();
            String name = data[1].trim();
            String type = data[4].trim();
            
            //find score by ID or name
            double avg = analytics.calculateStudentAverage(id);
            if (avg <= 0) avg = analytics.calculateStudentAverage(name);

            String scoreStr = (avg > 0) ? String.format("%.2f", avg) : "PENDING";
            
            report.append(String.format("%-8s | %-15s | %-10s | %-10s\n", id, name, type, scoreStr));
        }
    }
    report.append("============================================================\n");
    return report.toString();
}
}