package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import simu.framework.IEngine;
import simu.model.*;
import view.ISimulatorUI;
import view.Visualisation;
//import view.StoreVisualization;
//import simu.data.DatabaseManager;
//import simu.data.SimulationConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements IControllerVtoM, IControllerMtoV {
    private IEngine engine;
    private ISimulatorUI ui;
    private boolean paused = false;

    // FXML UI components
    @FXML
    private TextField simulationTimeField;
    @FXML
    private TextField delayField;
	@FXML
	private TextField configNameField;
    @FXML
    private Label resultsLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button resumeButton;
    @FXML
    private Button slowButton;
    @FXML
    private Button speedUpButton;

    // Customer tracking for visualization
    private Map<Integer, Customer> activeCustomers;
    private Map<ServicePointType, Integer> queueSizes;

    // TODO: Database manager for data persistence
    //private DatabaseManager dbManager;

    // TODO: Configuration settings
    //private SimulationConfig config;

    // Current simulation run ID
    private int currentRunId = -1;

    // No-arg constructor for FXML
    public Controller() {
        this.activeCustomers = new ConcurrentHashMap<>();
        this.queueSizes = new ConcurrentHashMap<>();

        // Initialize queue sizes
        for (ServicePointType type : ServicePointType.values()) {
            queueSizes.put(type, 0);
        }
    }

    public void setUI(ISimulatorUI ui) {
        this.ui = ui;
    }

    /* Engine control methods with FXML annotations */
    @FXML
    public void startSimulation() {
        engine = new MyEngine(this);

        engine.setSimulationTime(ui.getTime());
        engine.setDelay(ui.getDelay());

        ui.getVisualisation().clearDisplay();

        activeCustomers.clear();
        for (ServicePointType type : ServicePointType.values()) {
            queueSizes.put(type, 0);
        }

        // Update button states
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        resumeButton.setDisable(true);

        // Start the engine
        ((Thread) engine).start();
    }

    @FXML
    public void pauseSimulation() {
        if (engine != null && !paused) {
            paused = true;
            engine.setDelay(Long.MAX_VALUE / 2); // Very long delay effectively pauses

            // Update button states
            pauseButton.setDisable(true);
            resumeButton.setDisable(false);
        }
    }

    @FXML
    public void resumeSimulation() {
        if (engine != null && paused) {
            paused = false;
            engine.setDelay(ui.getDelay());

            // Update button states
            resumeButton.setDisable(true);
            pauseButton.setDisable(false);
        }
    }

    @FXML
    public void decreaseSpeed() {
        if (engine != null && !paused) {
            engine.setDelay((long) (engine.getDelay() * 1.10));
        }
    }

    @FXML
    public void increaseSpeed() {
        if (engine != null && !paused) {
            engine.setDelay((long) (engine.getDelay() * 0.9));
        }
    }

    /* Configuration methods */
	@FXML
	public void saveConfiguration() {
		String name = configNameField.getText();
		// config.setName(name);
		// dbManager.saveConfiguration(config);
	}

	@FXML
	public void loadConfiguration() {
//		int configId = getSelectedConfigId();
		// config = dbManager.loadConfiguration(configId);
	}
//
//	public SimulationConfig getConfig() {
//		return config;
//	}
//
//	public void setConfig(SimulationConfig config) {
//		this.config = config;
//	}

    /* Visualization methods from the engine */
    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> {
            ui.setEndingTime(time);
        });
    }

    @Override
    public void customerCreated(Customer customer) {
        // Add to tracking
        activeCustomers.put(customer.getId(), customer);
        System.out.println("Controller tracking customer #" + customer.getId());

        // Update visualization
        Platform.runLater(() -> {
            if (ui.getVisualisation() instanceof Visualisation vis) {
                vis.addNewCustomer(
                        customer.getId(),
                        customer.getType(),
                        customer.getItems(),
                        customer.getCurrentLocation()
                );
            }
        });
    }
    @Override
    public void customerMoved(int customerId, ServicePointType from, ServicePointType to) {
        Platform.runLater(() -> {
            if (ui.getVisualisation() instanceof Visualisation vis) {
                vis.moveCustomer(customerId, from, to);
                if (queueSizes.containsKey(to)) {
                    vis.incrementQueueSize(to);
                }
            }
        });
    }
    @Override
    public void customerCompleted(int customerId, ServicePointType type) {
        // Remove from tracking
        Customer customer = activeCustomers.remove(customerId);

        // Update visualization
        Platform.runLater(() -> {
            if (ui.getVisualisation() instanceof Visualisation vis) {
                vis.removeCustomer(customerId);
                if (queueSizes.containsKey(type)) {
                    vis.decrementQueueSize(type);
                }
            }
        });
    }
}