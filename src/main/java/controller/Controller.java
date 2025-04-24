package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import simu.framework.IEngine;
import simu.model.*;
import view.ISimulatorUI;
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
	@FXML private TextField simulationTimeField;
	@FXML private TextField delayField;
	@FXML private Label resultsLabel;
	@FXML private Button startButton;
	@FXML private Button pauseButton;
	@FXML private Button resumeButton;
	@FXML private Button slowButton;
	@FXML private Button speedUpButton;

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

	// Set UI reference if created via FXML
	public void setUI(ISimulatorUI ui) {
		this.ui = ui;
	}

	/* Engine control methods with FXML annotations */
	@FXML
	public void startSimulation() {
		// Create a new engine for this simulation run
		engine = new MyEngine(this);

		// Apply configuration to engine
		engine.setSimulationTime(ui.getTime());
		engine.setDelay(ui.getDelay());

		// Clear visualization
		ui.getVisualisation().clearDisplay();

		// Clear tracking collections
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
			engine.setDelay((long)(engine.getDelay()*1.10));
		}
	}

	@FXML
	public void increaseSpeed() {
		if (engine != null && !paused) {
			engine.setDelay((long)(engine.getDelay()*0.9));
		}
	}

	/* Configuration methods */
//	public void saveConfiguration(String name) {
//		config.setName(name);
//		dbManager.saveConfiguration(config);
//	}
//
//	public void loadConfiguration(int configId) {
//		config = dbManager.loadConfiguration(configId);
//	}
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
	public void visualiseCustomer() {
		// This is called when a customer is created in the old code
		// We'll handle this differently with our new visualization methods
	}

	/**
	 * Notify that a new customer has been created
	 */
//	public void customerCreated(Customer customer) {
//		// Add to tracking
//		activeCustomers.put(customer.getId(), customer);
//
//		// Update visualization on JavaFX thread
//		Platform.runLater(() -> {
//			if (ui.getVisualisation() instanceof StoreVisualization) {
//				StoreVisualization vis = (StoreVisualization) ui.getVisualisation();
//				vis.addCustomer(customer.getId(), customer.getType(), customer.getItemCount());
//
//				// Save customer to database
//				if (currentRunId != -1) {
//					dbManager.addCustomer(currentRunId, customer);
//				}
//			}
//		});
//	}

	/**
	 * Notify that a customer has moved to a new service point
	 */
//	public void customerMoved(int customerId, ServicePointType from, ServicePointType to) {
//		Platform.runLater(() -> {
//			if (ui.getVisualisation() instanceof StoreVisualization) {
//				StoreVisualization vis = (StoreVisualization) ui.getVisualisation();
//
//				// Determine the path key
//				String pathKey = from.toString() + "_TO_" + to.toString();
//				vis.moveCustomer(customerId, pathKey);
//
//				// Update customer in database
//				if (currentRunId != -1 && activeCustomers.containsKey(customerId)) {
//					dbManager.updateCustomerMovement(currentRunId, customerId, from, to);
//				}
//			}
//		});
//	}

	/**
	 * Notify that a customer has joined a queue
	 */
//	public void customerJoinedQueue(int customerId, ServicePointType servicePoint) {
//		// Update queue size tracking
//		int currentSize = queueSizes.getOrDefault(servicePoint, 0);
//		queueSizes.put(servicePoint, currentSize + 1);
//
//		// Update visualization
//		Platform.runLater(() -> {
//			if (ui.getVisualisation() instanceof StoreVisualization) {
//				StoreVisualization vis = (StoreVisualization) ui.getVisualisation();
//				vis.positionInQueue(customerId, servicePoint, currentSize);
//				vis.updateQueueCounters(new HashMap<>(queueSizes));
//			}
//		});
//	}

	/**
	 * Notify that a customer has left a queue
	 */
//	public void customerLeftQueue(int customerId, ServicePointType servicePoint) {
//		// Update queue size tracking
//		int currentSize = queueSizes.getOrDefault(servicePoint, 0);
//		if (currentSize > 0) {
//			queueSizes.put(servicePoint, currentSize - 1);
//		}
//
//		// Update visualization
//		Platform.runLater(() -> {
//			if (ui.getVisualisation() instanceof StoreVisualization) {
//				StoreVisualization vis = (StoreVisualization) ui.getVisualisation();
//				vis.updateQueueCounters(new HashMap<>(queueSizes));
//			}
//		});
//	}

	/**
	 * Notify that a customer has completed shopping and exited
	 */
//	public void customerCompleted(int customerId) {
//		// Remove from tracking
//		Customer customer = activeCustomers.remove(customerId);
//
//		// Update visualization
//		Platform.runLater(() -> {
//			if (ui.getVisualisation() instanceof StoreVisualization) {
//				StoreVisualization vis = (StoreVisualization) ui.getVisualisation();
//				vis.removeCustomer(customerId);
//
//				// Update customer completion in database
//				if (currentRunId != -1 && customer != null) {
//					dbManager.completeCustomer(currentRunId, customer);
//				}
//			}
//		});
//	}

	/**
	 * Update service point statistics
	 */
//	public void updateServicePointStats(ServicePointType type, ServicePointStats stats) {
//		if (currentRunId != -1) {
//			dbManager.updateServicePointStats(currentRunId, type, stats);
//		}
//	}
}