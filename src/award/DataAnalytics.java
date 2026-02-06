package award;

import shared.Constants;
import java.util.*;
import core.FileHandler;

//PATTERN: singleton
public class DataAnalytics {

    private static DataAnalytics instance;

    private DataAnalytics() {}

    public static DataAnalytics getInstance() {
        if (instance == null) {
            instance = new DataAnalytics();
        }
        return instance;
    }

    public double calculateStudentAverage(String studentID) {
        ArrayList<Double> scores = getScoresForStudent(studentID);
        if (scores.isEmpty()) return 0.0;

        double sum = 0;
        for (double s : scores) sum += s;
        return sum / scores.size();
    }

    private ArrayList<Double> getScoresForStudent(String studentID) {
    ArrayList<Double> scores = new ArrayList<>();
    List<String> lines = FileHandler.readAllLines(Constants.EVALUATIONS_FILE);

    for (String line : lines) {
        String[] data = line.split("\\" + Constants.DELIMITER);
        
        if (data.length >= 7 && data[1].trim().equals(studentID)) {
            try {
                scores.add(Double.parseDouble(data[6].trim()));
            } catch (Exception e) { System.err.println("Error: invalid score format for student " + studentID); }
        }
    }
    return scores;
}

    public double getHighestScore() {
        double max = 0.0;
        List<String> lines = FileHandler.readAllLines(Constants.EVALUATIONS_FILE);

        for (String line : lines) {
            String[] data = line.split("\\" + Constants.DELIMITER);
            if (data.length >= 7) {
                try {
                    int totalIndex = data.length - 2;
                    double score = Double.parseDouble(data[totalIndex].trim());
                    if (score > max) max = score;
                } catch (Exception e) { }
            }
        }
        return max;
    }
}
