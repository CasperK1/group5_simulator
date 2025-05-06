package view;

import java.text.DecimalFormat;
import controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import simu.data.SimulationConfig;
import simu.framework.Trace;
import simu.framework.Trace.Level;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;

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
		try {
			primaryStage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });

			primaryStage.setTitle("Checkout Simulator");
			String imagePath = "/customer.jpg";
			if (getClass().getResource(imagePath) != null) {
				primaryStage.getIcons().add(new Image(getClass().getResource(imagePath).toExternalForm()));
			}

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
			SimulationConfig config = controller.getConfig();
			delay.setText(String.valueOf(config.getDefaultDelay()));
			results = (Label) root.lookup("#resultsLabel");

			// Set up visualization
			StackPane visualizationContainer = (StackPane) root.lookup("#visualizationContainer");
			Canvas canvas = (Canvas) root.lookup("#storeCanvas");
			display = new Visualisation((int)canvas.getWidth(), (int)canvas.getHeight());
			visualizationContainer.getChildren().clear();
			visualizationContainer.getChildren().add((Canvas)display);

			Scene scene = new Scene(root);

			scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
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