package award;

import shared.Constants;
import core.FileHandler;

//PATTERN: Facade
public class SystemAnalyticsFacade {

    private AwardSystem awardSystem;
    private ReportGenerator reportGenerator;
    private ExportManager exportManager;

    public SystemAnalyticsFacade() {
        this.awardSystem = new AwardSystem();
        this.reportGenerator = new ReportGenerator();
        this.exportManager = new ExportManager();
    }

    //string for report screen
    public String generateFullReport() {
        return reportGenerator.generateGeneralReport();
    }

    //winners for dashbaord
    public String getOralWinner() {
        return awardSystem.determineWinner("Oral");
    }

    public String getPosterWinner() {
        return awardSystem.determineWinner("Poster");
    }

    //export features
    public void exportReportToText(String filename) {
        String content = reportGenerator.generateGeneralReport();
        exportManager.exportReport(content, filename);
    }

    public void exportDataToExcel(String filename) {//CSV format for excel 
        String content = reportGenerator.generateGeneralReport().replace("|", ",");
        exportManager.exportDataToCSV(content, filename);
    }
}
