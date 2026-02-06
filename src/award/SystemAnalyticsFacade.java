package award;

import shared.Constants;
// PATTERN:Facade
public class SystemAnalyticsFacade {

    private final AwardSystem awardSystem;
    private final ReportGenerator reportGenerator;
    private final  ExportManager exportManager;

    public SystemAnalyticsFacade() {
        this.awardSystem = new AwardSystem();
        this.reportGenerator = new ReportGenerator();
        this.exportManager = new ExportManager();
    }

    //string for report screen
    public String generateFullReport() {
        return reportGenerator.generateGeneralReport();
    }

    //get winners for dashboard
    public String getOralWinner() {
        return awardSystem.determineWinner(Constants.PRESENTATION_ORAL);
    }

    public String getPosterWinner() {
        return awardSystem.determineWinner(Constants.PRESENTATION_POSTER);
    }

    //export features
    public void exportReportToText(String filename) {
        String content = reportGenerator.generateGeneralReport();
        exportManager.exportReport(content, filename);
    }

    public void exportDataToExcel(String filename) {
        //converts report data into CSV format for excel
        String content = reportGenerator.generateGeneralReport().replace("|", ",");
        exportManager.exportDataToCSV(content, filename);
    }
}