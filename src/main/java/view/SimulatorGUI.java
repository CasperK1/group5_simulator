package view;

import java.text.DecimalFormat;
import controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import simu.framework.Trace;
import simu.framework.Trace.Level;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class SimulatorGUI extends Application implements ISimulatorUI {
	// Controller object (UI needs)
	private IControllerVtoM controller;
	// UI Components:
	private TextField time;
	private TextField delay;
	private Label results;

	private IVisualisation display;


	@Override
	public void init(){
		Trace.setTraceLevel(Level.INFO);
	}

	@Override
	public void start(Stage primaryStage) {
		// UI creation
		try {
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					Platform.exit();
					System.exit(0);
				}
			});

			primaryStage.setTitle("Simulator");

			// Load the FXML file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/SimulatorGUI.fxml"));
			Parent root = loader.load();

			// Get the controller instance from the loader
			Controller fxmlController = loader.getController();

			// Initialize controller with this UI
			fxmlController.setUI(this);
			this.controller = fxmlController;

			time = (TextField) root.lookup("#simulationTimeField");
			delay = (TextField) root.lookup("#delayField");
			results = (Label) root.lookup("#resultsLabel");

			// Set up visualization
			Canvas canvas = (Canvas) root.lookup("#storeCanvas");
			display = new Visualisation((int)canvas.getWidth(), (int)canvas.getHeight());

			Scene scene = new Scene(root, 800, 600);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/* UI interface methods (controller calls) */
	@Override
	public double getTime(){
		return Double.parseDouble(time.getText());
	}

	@Override
	public long getDelay(){
		return Long.parseLong(delay.getText());
	}

	@Override
	public void setEndingTime(double time) {
		DecimalFormat formatter = new DecimalFormat("#0.00");
		this.results.setText(formatter.format(time));
	}

	@Override
	public IVisualisation getVisualisation() {
		return display;
	}

	/* JavaFX-application (UI) start-up */
	public static void main(String[] args) {
		launch(args);
	}
}