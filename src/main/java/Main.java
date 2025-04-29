import utils.CSVReport;
import view.SimulatorGUI;

public class Main {
	// Simulator using Java FX
	public static void main(String args[]) {
		CSVReport.resetReportFile();
		SimulatorGUI.main(args);
	}
}
