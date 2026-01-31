package shared.Award;

public class IntegrationTester {

    public static void main(String[] args) {
        System.out.println("Starting Integration Test (Package: Award)...");

        // 1. Initialize Facade
        SystemAnalyticsFacade facade = new SystemAnalyticsFacade();

        // 2. Test Report
        System.out.println("\n--- Generating Report ---");
        System.out.println(facade.generateFullReport());

        // 3. Test Awards
        System.out.println("\n--- Calculating Awards ---");
        System.out.println("Best Oral: " + facade.getOralWinner());
        System.out.println("Best Poster: " + facade.getPosterWinner());

        System.out.println("Test Complete.");
    }
}