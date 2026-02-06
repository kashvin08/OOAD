package evaluator;


public class RubricSystem {

    public static final String[] CRITERIA = {
        "Problem Clarity", "Methodology", "Results", "Presentation"
    };

    public static final String[] RATINGS = {
        "1 - Very Poor", "2 - Poor", "3 - Average", "4 - Good", "5 - Perfect"
    };

    public int getScoreValue(String ratingString) {
        if (ratingString == null) {
            return 0;
        }
        String numberPart = ratingString.split(" - ")[0];
        return Integer.parseInt(numberPart);
    }

    public int calculateTotal(int[] scores) {
        int total = 0;
        for (int s : scores) {
            total += s;
        }
        return total;
    }
}
