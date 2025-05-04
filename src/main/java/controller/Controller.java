package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import simu.framework.IEngine;
import simu.model.*;
import view.ISimulatorUI;
import view.SimulatorGUI;
import view.Visualisation;
import simu.data.SimulationConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller class implementing both IControllerVtoM and IControllerMtoV interfaces.
 * Manages communication between the view and model components of the simulation,
 * handling user interactions from the UI and updating the view with model data.
 */
public class Controller implements IControllerVtoM, IControllerMtoV {
    public Button resetButton;
    private IEngine engine;
    private ISimulatorUI ui;
    private boolean paused = false;
    private final SimulationConfig config = new SimulationConfig();

    // FXML UI components
    @FXML private TextField simulationTimeField;
    @FXML private TextField delayField;
    @FXML private Label resultsLabel;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button resumeButton;
    @FXML private Button slowButton;
    @FXML private Button speedUpButton;
    @FXML private Button getResetButton;

    // Configuration tab fields
    @FXML private ComboBox<String> arrivalDistributionCombo;
    @FXML private TextField arrivalParamField;
    @FXML private Slider expressCustomerSlider;
    @FXML private Label expressPercentLabel;
    @FXML private TextField minRegularItems;
    @FXML private TextField maxRegularItems;
    @FXML private TextField minExpressItems;
    @FXML private TextField maxExpressItems;
    @FXML private ComboBox<String> serviceDistributionCombo;
    @FXML private TextField serviceParamField;
    @FXML private TextField shoppingMultiplier;
    @FXML private TextField regularMultiplier;
    @FXML private TextField expressMultiplier;
    @FXML private TextField selfCheckoutMultiplier;
    @FXML private TextField configNameField;

    // Customer tracking for visualization
    private Map<Integer, Customer> activeCustomers = new ConcurrentHashMap<>();
    private Map<ServicePointType, Integer> queueSizes = new ConcurrentHashMap<>();

    /**
     * Initializes the controller.
     * Sets up initial queue sizes for all service point types.
     */
    @FXML
    public void initialize() {
        // Initialize queue sizes
        for (ServicePointType type : ServicePointType.values()) {
            queueSizes.put(type, 0);
        }
        initializeConfigControls();
    }

    private void initializeConfigControls() {
        // Set up distribution options
        arrivalDistributionCombo.getItems().addAll("Negexp", "Normal", "Uniform");
        serviceDistributionCombo.getItems().addAll("Normal", "Negexp", "Uniform");

        // Set default values
        arrivalDistributionCombo.setValue(config.getArrivalDistribution());
        serviceDistributionCombo.setValue(config.getServiceDistribution());


        // Arrival distribution change listener
        arrivalDistributionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                config.setArrivalDistribution(newVal);
            }
        });

        // Arrival parameter change listener
        arrivalParamField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                if (value > 0) {
                    config.setArrivalParam(value);
                }
            } catch (NumberFormatException e) {
                // Revert to old value if invalid
                arrivalParamField.setText(oldVal);
            }
        });

        // Express customer percentage slider
        expressCustomerSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double percentage = newVal.doubleValue();
            expressPercentLabel.setText(String.format("%.0f%%", percentage));
            config.setExpressCustomerPercentage(percentage);
        });

        // Service distribution change listener
        serviceDistributionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                config.setServiceDistribution(newVal);
            }
        });

        // Customer items fields
        IntegerFieldController.setupPairedFields(
                minRegularItems, maxRegularItems,
                11, 50,
                config::setMinRegularItems, config::setMaxRegularItems
        );

        IntegerFieldController.setupPairedFields(
                minExpressItems, maxExpressItems,
                1, 10,
                config::setMinExpressItems, config::setMaxExpressItems
        );

        // Service parameter fields
        DoubleFieldController.setupField(serviceParamField, 0.1, 100, config::setServiceParam);
        DoubleFieldController.setupField(shoppingMultiplier, 0.1, 10, config::setShoppingMultiplier);
        DoubleFieldController.setupField(regularMultiplier, 0.1, 10, config::setRegularMultiplier);
        DoubleFieldController.setupField(expressMultiplier, 0.1, 10, config::setExpressMultiplier);
        DoubleFieldController.setupField(selfCheckoutMultiplier, 0.1, 10, config::setSelfCheckoutMultiplier);

        delayField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Delay field changed to: " + newValue);
            try {
                long delay = Long.parseLong(newValue);
                if (engine != null) {
                    engine.setDelay(delay);
                    System.out.println("Engine delay updated to: " + delay + " ms");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid delay input: " + newValue);
            }
        });
    }

    /**
     * Sets the UI component for this controller.
     *
     * @param ui The simulator UI instance
     */
    public void setUI(ISimulatorUI ui) {
        this.ui = ui;
    }

    /**
     * Starts the simulation with the current configuration.
     * Creates a new engine, sets simulation parameters, and starts the simulation thread.
     */
    @FXML
    public void startSimulation() {
        // Create engine with the current configuration
        engine = new MyEngine(this, config);

        double simTime = Double.parseDouble(simulationTimeField.getText());
        long delayTime = Long.parseLong(delayField.getText());

        engine.setSimulationTime(simTime);
        engine.setDelay(delayTime);

        if (ui != null) {
            ui.getVisualisation().clearDisplay();
        }
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

    /**
     * Pauses the running simulation.
     * Updates UI button states and sets the engine to paused state.
     */
    @FXML
    public void pauseSimulation() {
        if (engine != null && !paused) {
            paused = true;
            long originalDelay = engine.getDelay();
            engine.setPaused(true);

            // Update button states
            pauseButton.setDisable(true);
            resumeButton.setDisable(false);
        }
    }

    /**
     * Resumes a paused simulation.
     * Updates UI button states and sets the engine to resume execution.
     */
    @FXML
    public void resumeSimulation() {
        if (engine != null && paused) {
            paused = false;
            engine.setPaused(false);

            // Update button states
            resumeButton.setDisable(true);
            pauseButton.setDisable(false);
        }
    }

    /**
     * Decreases the simulation speed by increasing the delay between simulation steps.
     */
    @FXML
    public void decreaseSpeed() {
        if (engine != null && !paused) {
            long newDelay = (long) (engine.getDelay() + 20);
            engine.setDelay(newDelay);
            delayField.setText(String.valueOf(newDelay));
            System.out.println("Delay decreased to: " + engine.getDelay() + " ms");
        }
    }

    /**
     * Increases the simulation speed by decreasing the delay between simulation steps.
     */
    @FXML
    public void increaseSpeed() {
        if (engine != null && !paused) {
            long newDelay = (long) (engine.getDelay() - 20);
            engine.setDelay(newDelay);
            delayField.setText(String.valueOf(newDelay));
            System.out.println("Delay increased to: " + engine.getDelay() + " ms");
        }
    }

    @FXML
    public void resetSimulation() {
        if (engine != null) {
            engine.reset(); // call reset on engine and everything inside (event list, clock, etc.)
        }

        activeCustomers.clear(); // clear customer tracking
        Customer.resetStatistics();
        // Reset queue sizes
        for (ServicePointType type : ServicePointType.values()) {
            queueSizes.put(type, 0);
        }

        // Reset GUI
        if (ui != null) {
            ui.getVisualisation().resetDisplay();
        }

        resultsLabel.setText("0.00");
        delayField.setText(String.valueOf(config.getDefaultDelay()));
        simulationTimeField.setText(String.valueOf(config.getDefaultSimulationTime()));

        // Reset buttons
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        resumeButton.setDisable(true);

        paused = false;
    }


    /**
     * Saves the current simulation configuration.
     * Currently shows an alert that configuration is saved without actual database storage.
     */
    @FXML
    public void saveConfiguration() {
        // For now, just show an alert that config is saved (without DB)
        String name = configNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning",
                    "Please enter a configuration name.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Configuration Saved",
                "Configuration '" + name + "' has been saved.");
    }

    /**
     * Loads a saved simulation configuration.
     * Currently a placeholder that shows an alert message.
     */
    @FXML
    public void loadConfiguration() {
        // placeholder message
        showAlert(Alert.AlertType.INFORMATION, "Load Configuration",
                "This feature will be implemented");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Gets the current simulation configuration.
     *
     * @return The current SimulationConfig object
     */
    public SimulationConfig getConfig() {
        return config;
    }

    /**
     * Shows the simulation end time in the UI.
     * Updates the results label with formatted time value.
     *
     * @param time The final simulation time
     */
    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> {
            resultsLabel.setText(String.format("%.2f", time));
        });
    }

    /**
     * Notifies the UI that a new customer has been created.
     * Adds the customer to tracking and updates the visualization.
     *
     * @param customer The newly created customer
     */
    @Override
    public void customerCreated(Customer customer) {
        // Add to tracking
        activeCustomers.put(customer.getId(), customer);
        System.out.println("Controller tracking customer #" + customer.getId());

        // Update visualization
        Platform.runLater(() -> {
            if (ui != null && ui.getVisualisation() instanceof Visualisation vis) {
                vis.addNewCustomer(
                        customer.getId(),
                        customer.getType(),
                        customer.getItems(),
                        customer.getCurrentLocation()
                );
            }
        });
    }

    /**
     * Notifies the UI that a customer has moved from one service point to another.
     * Updates the visualization to show the customer's new position.
     *
     * @param customerId The ID of the customer that moved
     * @param from The service point type the customer moved from
     * @param to The service point type the customer moved to
     */
    @Override
    public void customerMoved(int customerId, ServicePointType from, ServicePointType to) {
        Platform.runLater(() -> {
            if (ui != null && ui.getVisualisation() instanceof Visualisation vis) {
                vis.moveCustomer(customerId, from, to);
                if (queueSizes.containsKey(to)) {
                    vis.incrementQueueSize(to);
                }
            }
        });
    }

    /**
     * Notifies the UI that a customer has completed service and is leaving the system.
     * Removes the customer from visualization and updates queue sizes.
     *
     * @param customerId The ID of the customer that completed service
     * @param type The service point type the customer was at when completing service
     */
    @Override
    public void customerCompleted(int customerId, ServicePointType type) {
        // Update visualization
        Platform.runLater(() -> {
            if (ui != null && ui.getVisualisation() instanceof Visualisation vis) {
                vis.removeCustomer(customerId);
                if (queueSizes.containsKey(type)) {
                    vis.decrementQueueSize(type);
                }
            }
        });
    }

}