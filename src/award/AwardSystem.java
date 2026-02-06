package award;

import shared.Constants;
import core.FileHandler;
import java.util.List;

public class AwardSystem {

    public AwardSystem() {}

    public String determineWinner(String category) {
    String winnerName = "No Candidate";
    double highestScore = -1.0;
    DataAnalytics analytics = DataAnalytics.getInstance();

    List<String> lines = FileHandler.readAllLines(shared.Constants.SUBMISSIONS_FILE);
    for (String line : lines) {
        String[] data = line.split("\\" + shared.Constants.DELIMITER);
        if (data.length >= 5 && data[4].trim().equalsIgnoreCase(category)) {//check score for both ID & name
            double score = analytics.calculateStudentAverage(data[0].trim());
            if (score <= 0) score = analytics.calculateStudentAverage(data[1].trim());

            if (score > highestScore) {
                highestScore = score;
                winnerName = data[1].trim();
            }
        }
    }
    return (highestScore > 0) ? winnerName + " (" + highestScore + "/20)" : "No Graded Candidates";
}
    public String getPeoplesChoice() {
    return "Student with most votes/evaluations";
}
   
}
