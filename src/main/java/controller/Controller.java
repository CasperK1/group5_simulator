package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import simu.data.ConfigManager;
import simu.framework.Clock;
import simu.framework.IEngine;
import simu.model.*;
import view.ISimulatorUI;
import view.SimulatorGUI;
import view.Visualisation;
import simu.data.SimulationConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    // FXML Statistics components
    @FXML private Label totalCustomersLabel;
    @FXML private Label avgWaitTimeLabel;
    @FXML private Label maxQueueLabel;

    @FXML private Label entranceCustomersLabel;
    @FXML private Label shoppingCustomersLabel;
    @FXML private Label regularCustomersLabel;
    @FXML private Label expressCustomersLabel;
    @FXML private Label selfCheckoutCustomersLabel;
    @FXML private Label selfCheckoutServiceTimeLabel;
    @FXML private Label selfCheckoutUtilizationLabel;

    @FXML private Label entranceServiceTimeLabel;
    @FXML private Label shoppingServiceTimeLabel;
    @FXML private Label regularServiceTimeLabel;
    @FXML private Label expressServiceTimeLabel;

    @FXML private Label entranceUtilizationLabel;
    @FXML private Label shoppingUtilizationLabel;
    @FXML private Label regularUtilizationLabel;
    @FXML private Label expressUtilizationLabel;

    @FXML private LineChart<Number, Number> queueLengthChart;

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
    @FXML private ComboBox<String> savedConfigsCombo;

    // Line Chart
    private XYChart.Series<Number, Number> queueLengthSeries = new XYChart.Series<>();

    // Customer tracking for visualization
    private Map<Integer, Customer> activeCustomers = new ConcurrentHashMap<>();
    private Map<ServicePointType, Integer> queueSizes = new ConcurrentHashMap<>();
    private Map<ServicePointType, Integer> servicePointCustomerCount = new HashMap<>();
    private Map<ServicePointType, Double> servicePointServiceTime = new HashMap<>();


    private int maxQueueLength = 0;

    /**
     * Initializes the controller.
     * Sets up initial queue sizes for all service point types.
     */
    @FXML
    public void initialize() {
        // Initialize queue sizes
        for (ServicePointType type : ServicePointType.values()) {
            queueSizes.put(type, 0);
            servicePointCustomerCount.put(type, 0);
            servicePointServiceTime.put(type, 0.0);
        }
        initializeConfigControls();
        loadSavedConfigList();

        queueLengthSeries = new XYChart.Series<>();
        queueLengthSeries.setName("Queue Length");
        queueLengthChart.getData().add(queueLengthSeries);
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
            servicePointCustomerCount.put(type, 0);
            servicePointServiceTime.put(type, 0.0);
        }
        queueLengthSeries.getData().clear();
        maxQueueLength = 0;
        Customer.resetStatistics();

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
     * Config alert dialog to show messages to the user.
     */

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Loads the list of saved configurations and populates the ComboBox.
     */
    private void loadSavedConfigList() {
        try {
            List<String> configNames = ConfigManager.getSavedConfigurationNames();
            savedConfigsCombo.getItems().clear();
            savedConfigsCombo.getItems().addAll(configNames);

            if (!configNames.isEmpty()) {
                savedConfigsCombo.setPromptText("Select a configuration");
            } else {
                savedConfigsCombo.setPromptText("No saved configurations");
            }
        } catch (IOException e) {
            System.err.println("Error loading configuration list: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to load saved configurations: " + e.getMessage());
        }
    }

    /**
     * Updates the UI controls to reflect the current configuration.
     */
    private void updateUIFromConfig() {
        // Arrival settings
        arrivalDistributionCombo.setValue(config.getArrivalDistribution());
        arrivalParamField.setText(String.valueOf(config.getArrivalParam()));

        // Customer parameters
        expressCustomerSlider.setValue(config.getExpressCustomerPercentage());
        expressPercentLabel.setText(String.format("%.0f%%", config.getExpressCustomerPercentage()));
        minRegularItems.setText(String.valueOf(config.getMinRegularItems()));
        maxRegularItems.setText(String.valueOf(config.getMaxRegularItems()));
        minExpressItems.setText(String.valueOf(config.getMinExpressItems()));
        maxExpressItems.setText(String.valueOf(config.getMaxExpressItems()));

        // Service point parameters
        serviceDistributionCombo.setValue(config.getServiceDistribution());
        serviceParamField.setText(String.valueOf(config.getServiceParam()));
        shoppingMultiplier.setText(String.valueOf(config.getShoppingMultiplier()));
        regularMultiplier.setText(String.valueOf(config.getRegularMultiplier()));
        expressMultiplier.setText(String.valueOf(config.getExpressMultiplier()));
        selfCheckoutMultiplier.setText(String.valueOf(config.getSelfCheckoutMultiplier()));

        // Default values
        delayField.setText(String.valueOf(config.getDefaultDelay()));
    }

    /**
     * Saves the current simulation configuration.
     * Saves the configuration to a properties file with the specified name.
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

        // Check if configuration already exists
        try {
            List<String> existingConfigs = ConfigManager.getSavedConfigurationNames();
            if (existingConfigs.contains(name)) {
                // Confirm overwrite
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Overwrite");
                confirmAlert.setHeaderText("Configuration already exists");
                confirmAlert.setContentText("Do you want to overwrite the existing configuration '" + name + "'?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    return;
                }
            }

            // Save configuration
            ConfigManager.saveConfiguration(config, name);

            // Refresh the list of saved configurations
            loadSavedConfigList();

            // Select the newly saved configuration
            savedConfigsCombo.setValue(name);

            showAlert(Alert.AlertType.INFORMATION, "Configuration Saved",
                    "Configuration '" + name + "' has been saved successfully.");

        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to save configuration: " + e.getMessage());
        }
    }

    /**
     * Loads a saved simulation configuration.
     * Loads the configuration selected in the ComboBox.
     */
    @FXML
    public void loadConfiguration() {
        String selectedConfig = savedConfigsCombo.getValue();
        if (selectedConfig == null || selectedConfig.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Configuration Selected",
                    "Please select a configuration to load.");
            return;
        }

        try {
            // Load the selected configuration
            SimulationConfig loadedConfig = ConfigManager.loadConfiguration(selectedConfig);

            // Set the loaded configuration as current
            for (var field : loadedConfig.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    var value = field.get(loadedConfig);
                    field.set(config, value);
                } catch (IllegalAccessException e) {
                    System.err.println("Error copying field " + field.getName() + ": " + e.getMessage());
                }
            }

            // Update the UI to reflect the loaded configuration
            updateUIFromConfig();

            // Update the config name field
            configNameField.setText(selectedConfig);

            showAlert(Alert.AlertType.INFORMATION, "Configuration Loaded",
                    "Configuration '" + selectedConfig + "' has been loaded successfully.");

        } catch (FileNotFoundException e) {
            System.err.println("Configuration file not found: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Configuration file not found: " + selectedConfig);
            // Refresh the list to remove the missing configuration
            loadSavedConfigList();
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to load configuration: " + e.getMessage());
        }
    }

    /**
     * Deletes the selected configuration.
     */
    @FXML
    public void deleteConfiguration() {
        String selectedConfig = savedConfigsCombo.getValue();
        if (selectedConfig == null || selectedConfig.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Configuration Selected",
                    "Please select a configuration to delete.");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Configuration");
        confirmAlert.setContentText("Are you sure you want to delete the configuration '" + selectedConfig + "'?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = ConfigManager.deleteConfiguration(selectedConfig);

            if (deleted) {
                // Refresh the list of saved configurations
                loadSavedConfigList();
                showAlert(Alert.AlertType.INFORMATION, "Configuration Deleted",
                        "Configuration '" + selectedConfig + "' has been deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to delete configuration '" + selectedConfig + "'.");
            }
        }
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
     * Updates the time left display in the UI.
     * Called by the engine to provide real-time estimates.
     *
     * @param secondsLeft Estimated seconds left, or -1 if still calculating
     */
    @Override
    public void updateTimeLeft(int secondsLeft) {
        ui.updateTimeLeft(secondsLeft);
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
        Customer customer = activeCustomers.get(customerId);

        if (customer != null) {
            // Record timestamps for statistics
            double now = Clock.getInstance().getTime();

            // Start shopping timer when customer enters shopping
            if (to == ServicePointType.SHOPPING) {
                customer.startShopping();
            }

            // Track entrance service time when leaving entrance
            if (from == ServicePointType.ENTRANCE && to == ServicePointType.SHOPPING) {
                double entranceTime = now - customer.getArrivalTime();
                servicePointCustomerCount.merge(from, 1, Integer::sum);
                servicePointServiceTime.merge(from, entranceTime, Double::sum);
            }

            // End shopping and start checkout when moving to any checkout type
            if (from == ServicePointType.SHOPPING &&
                    (to == ServicePointType.REGULAR_CHECKOUT ||
                            to == ServicePointType.EXPRESS_CHECKOUT ||
                            to == ServicePointType.SELF_CHECKOUT)) {

                customer.endShopping();
                customer.startCheckout();

                double shoppingTime = customer.getShoppingDuration();
                servicePointCustomerCount.merge(from, 1, Integer::sum);
                servicePointServiceTime.merge(from, shoppingTime, Double::sum);
            }


            // UI & chart updates
            Platform.runLater(() -> {
                if (ui != null && ui.getVisualisation() instanceof Visualisation vis) {
                    vis.moveCustomer(customerId, from, to);

                    queueSizes.merge(to, 1, Integer::sum);
                    queueSizes.merge(from, -1, (oldValue, value) -> Math.max(0, oldValue + value));

                    vis.incrementQueueSize(to);
                    vis.decrementQueueSize(from); // <-- crucial to restore visual state

                    int totalQueue = totalQueueLength();
                    if (totalQueue > maxQueueLength) {
                        maxQueueLength = totalQueue;
                    }

                    double currentTime = Clock.getInstance().getTime();
                    queueLengthSeries.getData().add(new XYChart.Data<>(currentTime, totalQueue));

                    updateStatistics();
                }
            });
        }
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
        Customer customer = activeCustomers.remove(customerId);
        if (customer != null) {
            double now = Clock.getInstance().getTime();
            customer.setRemovalTime(now);

            customer.reportResults();

            // Calculate service duration depending on location
            double duration = 0.0;
            switch (type) {
                case REGULAR_CHECKOUT:
                case EXPRESS_CHECKOUT:
                case SELF_CHECKOUT: // ✅ Add this
                    duration = customer.getCheckoutDuration();
                    break;
                case SHOPPING:
                    duration = customer.getShoppingDuration();
                    break;
            }

            servicePointCustomerCount.merge(type, 1, Integer::sum);
            servicePointServiceTime.merge(type, duration, Double::sum);

            updateStatistics();
        }

        // UI cleanup
        Platform.runLater(() -> {
            if (ui != null && ui.getVisualisation() instanceof Visualisation vis) {
                vis.removeCustomer(customerId);
                queueSizes.merge(type, -1, (oldValue, value) -> Math.max(0, oldValue + value));
                vis.decrementQueueSize(type); // <-- add this after updating queue size
            }
        });
    }



    private int totalQueueLength() {
        return queueSizes.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void updateStatistics() {
        Platform.runLater(() -> {
            totalCustomersLabel.setText(String.valueOf(Customer.getLatestCustomerId()));
            avgWaitTimeLabel.setText(String.format("%.2f", Customer.getMeanServiceTime()));
            maxQueueLabel.setText(String.valueOf(maxQueueLength));

            updateServicePointStats(ServicePointType.ENTRANCE, entranceCustomersLabel, entranceServiceTimeLabel, entranceUtilizationLabel);
            updateServicePointStats(ServicePointType.SHOPPING, shoppingCustomersLabel, shoppingServiceTimeLabel, shoppingUtilizationLabel);
            updateServicePointStats(ServicePointType.REGULAR_CHECKOUT, regularCustomersLabel, regularServiceTimeLabel, regularUtilizationLabel);
            updateServicePointStats(ServicePointType.EXPRESS_CHECKOUT, expressCustomersLabel, expressServiceTimeLabel, expressUtilizationLabel);
            updateServicePointStats(ServicePointType.SELF_CHECKOUT, selfCheckoutCustomersLabel, selfCheckoutServiceTimeLabel, selfCheckoutUtilizationLabel); // <-- ADD THIS
        });
    }


    private void updateServicePointStats(ServicePointType type, Label customers, Label avgService, Label utilization) {
        // Live customer count at the service point
        int currentCount = queueSizes.getOrDefault(type, 0);

        // Completed customer stats
        int completed = servicePointCustomerCount.getOrDefault(type, 0);
        double totalServiceTime = servicePointServiceTime.getOrDefault(type, 0.0);
        double totalTime = Clock.getInstance().getTime();

        customers.setText(String.valueOf(currentCount));
        avgService.setText(completed > 0 ? String.format("%.2f", totalServiceTime / completed) : "0.00");
        utilization.setText(totalTime > 0 ? String.format("%.0f%%", (totalServiceTime / totalTime) * 100) : "0%");
    }
}