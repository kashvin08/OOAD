package shared.Award;

import shared.Constants;
import core.FileHandler;
import java.util.List;

public class AwardSystem {

    public AwardSystem() {}

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