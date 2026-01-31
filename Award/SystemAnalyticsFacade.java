package shared.Award;

import shared.Constants;
import core.FileHandler;

// PATTERN: Facade (Structural)
public class SystemAnalyticsFacade {

    private AwardSystem awardSystem;
    private ReportGenerator reportGenerator;
    private ExportManager exportManager;

    public SystemAnalyticsFacade() {
        this.awardSystem = new AwardSystem();
        this.reportGenerator = new ReportGenerator();
        this.exportManager = new ExportManager();
    }

    //Generate the string for the Report Screen
    public String generateFullReport() {
        return reportGenerator.generateGeneralReport();
    }

    //Get Winners for the Dashboard
    public String getOralWinner() {
        return awardSystem.determineWinner(Constants.PRESENTATION_ORAL);
    }

    public String getPosterWinner() {
        return awardSystem.determineWinner(Constants.PRESENTATION_POSTER);
    }

    //Export features
    public void exportReportToText(String filename) {
        String content = reportGenerator.generateGeneralReport();
        exportManager.exportReport(content, filename);
    }

    public void exportDataToExcel(String filename) {
        // Converts the report data into CSV format for Excel
        String content = reportGenerator.generateGeneralReport().replace("|", ",");
        exportManager.exportDataToCSV(content, filename);
    }
}