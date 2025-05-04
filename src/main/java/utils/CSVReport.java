package utils;

import simu.model.Customer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;

public class CSVReport {

    private static final String csvFile = "customer_report.csv";

    // Deletes the existing CSV file if it exists
    public static void resetReportFile() {
        File file = new File(csvFile);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Existing file deleted: " + csvFile);
            } else {
                System.err.println("Error deleting existing file: " + csvFile);
            }
        }
    }

    // Saves a customer's data to the CSV report
    public static void save(Customer customer, double meanServiceTime) {
        File file = new File(csvFile);
        boolean fileExists = file.exists();

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            // Write header if the file is new
            if (!fileExists) {
                writer.println("customer_id,customer_type,arrival_time,removal_time,total_time,items,mean_service_time(min)");
            }

            double arrivalTime = customer.getArrivalTime();
            double removalTime = customer.getRemovalTime();
            double totalTime = removalTime - arrivalTime;

            String arrivalTimeFormatted = convertToTimeFormat(arrivalTime);
            String removalTimeFormatted = convertToTimeFormat(removalTime);
            String totalTimeFormatted = convertToTimeFormat(totalTime);
            int meanServiceTimeInt = (int) meanServiceTime;

            writer.printf("%d,%s,%s,%s,%s,%d,%d%n",
                    customer.getId(),
                    customer.getType().toString(),
                    arrivalTimeFormatted,
                    removalTimeFormatted,
                    totalTimeFormatted,
                    customer.getItems(),
                    meanServiceTimeInt
            );
            sortCsvByCustomerId();

        } catch (IOException e) {
            System.err.println("Error writing report to CSV: " + e.getMessage());
        }
    }

    // Converts decimal time in minutes to hh:mm:ss
    private static String convertToTimeFormat(double timeInMinutes) {
        int totalSeconds = (int) (timeInMinutes * 60);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void sortCsvByCustomerId() {
        File file = new File(csvFile);
        if (!file.exists()) {
            System.err.println("CSV file does not exist.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() <= 1) return; // No data to sort

            String header = lines.remove(0); // Keep the header

            // Sort by customer_id (which is the first value in each line)
            lines.sort(Comparator.comparingInt(line -> Integer.parseInt(line.split(",")[0])));

            // Rewrite the file with sorted lines
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println(header); // Write header first
                for (String line : lines) {
                    writer.println(line); // Write sorted lines
                }
            }

        } catch (IOException e) {
            System.err.println("Error sorting CSV file: " + e.getMessage());
        }
    }
}
