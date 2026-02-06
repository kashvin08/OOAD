package award;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class ExportManager {

    private void writeToFile(String content, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
            JOptionPane.showMessageDialog(null, "Report exported to: " + new java.io.File(fileName).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error exporting: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error exporting file.");
        }
    }

    public void exportReport(String reportContent, String fileName) {
        if (!fileName.endsWith(".txt")) fileName += ".txt";
        writeToFile(reportContent, fileName);
    }

    public void exportDataToCSV(String csvContent, String fileName) {
        if (!fileName.endsWith(".csv")) fileName += ".csv";
        writeToFile(csvContent, fileName);
    }
}
