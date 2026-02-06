package award;

public class IntegrationTester {

    public static void main(String[] args) {
        System.out.println("Starting Integration Test (Package: Award)...");

//init facade
    SystemAnalyticsFacade facade = new SystemAnalyticsFacade();

//test report
        System.out.println("\n--- Generating Report ---");
        System.out.println(facade.generateFullReport());

//test award
        System.out.println("\n--- Calculating Awards ---");
        System.out.println("Best Oral: " + facade.getOralWinner());
        System.out.println("Best Poster: " + facade.getPosterWinner());

        System.out.println("Test Complete.");
    }
}