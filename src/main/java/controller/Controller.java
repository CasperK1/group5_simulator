package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import simu.framework.IEngine;
import simu.model.*;
import view.ISimulatorUI;
import view.Visualisation;
import simu.data.SimulationConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements IControllerVtoM, IControllerMtoV {
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
    }

    public void setUI(ISimulatorUI ui) {
        this.ui = ui;
    }

    /* Engine control methods */
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

    // Method to get current configuration
    public SimulationConfig getConfig() {
        return config;
    }

    /* Visualization methods from the engine */
    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> {
            resultsLabel.setText(String.format("%.2f", time));
        });
    }

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