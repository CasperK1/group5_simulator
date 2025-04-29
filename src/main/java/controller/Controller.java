package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import simu.data.SimulationConfig;
import simu.framework.Clock;
import simu.framework.IEngine;
import simu.model.*;
import view.ISimulatorUI;
import view.Visualisation;

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

    // Statistics UI
    @FXML private Label totalCustomersLabel;
    @FXML private Label avgWaitTimeLabel;
    @FXML private Label maxQueueLabel;

    @FXML private Label entranceCustomersLabel;
    @FXML private Label shoppingCustomersLabel;
    @FXML private Label regularCustomersLabel;
    @FXML private Label expressCustomersLabel;

    @FXML private Label entranceServiceTimeLabel;
    @FXML private Label shoppingServiceTimeLabel;
    @FXML private Label regularServiceTimeLabel;
    @FXML private Label expressServiceTimeLabel;

    @FXML private Label entranceUtilizationLabel;
    @FXML private Label shoppingUtilizationLabel;
    @FXML private Label regularUtilizationLabel;
    @FXML private Label expressUtilizationLabel;

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

    // Line Chart
    @FXML private LineChart<Number, Number> queueLengthChart;
    private XYChart.Series<Number, Number> queueLengthSeries = new XYChart.Series<>();

    // Customer tracking
    private Map<Integer, Customer> activeCustomers = new ConcurrentHashMap<>();
    private Map<ServicePointType, Integer> queueSizes = new ConcurrentHashMap<>();
    private Map<ServicePointType, Integer> servicePointCustomerCount = new ConcurrentHashMap<>();
    private Map<ServicePointType, Double> servicePointTotalTime = new ConcurrentHashMap<>();
    private int maxQueueLength = 0;

    @FXML
    public void initialize() {
        // Init queue sizes
        for (ServicePointType type : ServicePointType.values()) {
            queueSizes.put(type, 0);
            servicePointCustomerCount.put(type, 0);
            servicePointTotalTime.put(type, 0.0);
        }
        if (!queueLengthChart.getData().contains(queueLengthSeries)) {
            queueLengthChart.getData().add(queueLengthSeries);
        }
        queueLengthSeries.setName("Queue Length");

        initializeConfigControls();
    }

    private void initializeConfigControls() {
        arrivalDistributionCombo.getItems().addAll("Negexp", "Normal", "Uniform");
        serviceDistributionCombo.getItems().addAll("Normal", "Negexp", "Uniform");

        arrivalDistributionCombo.setValue(config.getArrivalDistribution());
        serviceDistributionCombo.setValue(config.getServiceDistribution());

        arrivalDistributionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) config.setArrivalDistribution(newVal);
        });

        serviceDistributionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) config.setServiceDistribution(newVal);
        });

        expressCustomerSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double percentage = newVal.doubleValue();
            expressPercentLabel.setText(String.format("%.0f%%", percentage));
            config.setExpressCustomerPercentage(percentage);
        });

        arrivalParamField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                config.setArrivalParam(Double.parseDouble(newVal));
            } catch (NumberFormatException ignored) {}
        });

        serviceParamField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                config.setServiceParam(Double.parseDouble(newVal));
            } catch (NumberFormatException ignored) {}
        });

        // Integer and Double field helpers
        IntegerFieldController.setupPairedFields(minRegularItems, maxRegularItems, 11, 50,
                config::setMinRegularItems, config::setMaxRegularItems);

        IntegerFieldController.setupPairedFields(minExpressItems, maxExpressItems, 1, 10,
                config::setMinExpressItems, config::setMaxExpressItems);

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
        Customer.resetStatistics();
        activeCustomers.clear();
        queueLengthSeries.getData().clear();
        maxQueueLength = 0;

        for (ServicePointType type : ServicePointType.values()) {
            queueSizes.put(type, 0);
            servicePointCustomerCount.put(type, 0);
            servicePointTotalTime.put(type, 0.0);
        }

        engine = new MyEngine(this, config);

        double simTime = Double.parseDouble(simulationTimeField.getText());
        long delayTime = Long.parseLong(delayField.getText());

        engine.setSimulationTime(simTime);
        engine.setDelay(delayTime);

        if (ui != null) {
            ui.getVisualisation().clearDisplay();
        }

        paused = false;

        startButton.setDisable(true);
        pauseButton.setDisable(false);
        resumeButton.setDisable(true);

        ((Thread) engine).start();
    }

    @FXML
    public void pauseSimulation() {
        if (engine != null && !paused) {
            paused = true;
            engine.setPaused(true);
            pauseButton.setDisable(true);
            resumeButton.setDisable(false);
        }
    }

    @FXML
    public void resumeSimulation() {
        if (engine != null && paused) {
            paused = false;
            engine.setPaused(false);
            resumeButton.setDisable(true);
            pauseButton.setDisable(false);
        }
    }

    @FXML
    public void decreaseSpeed() {
        if (engine != null && !paused) {
            engine.setDelay((long) (engine.getDelay() * 1.1));
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
        if (name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a configuration name.");
            return;
        }
        showAlert(Alert.AlertType.INFORMATION, "Configuration Saved", "Configuration '" + name + "' has been saved.");
    }

    @FXML
    public void loadConfiguration() {
        showAlert(Alert.AlertType.INFORMATION, "Load Configuration", "This feature will be implemented.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public SimulationConfig getConfig() {
        return config;
    }

    /* Engine Callbacks */
    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> resultsLabel.setText(String.format("%.2f", time)));
    }

    @Override
    public void customerCreated(Customer customer) {
        activeCustomers.put(customer.getId(), customer);

        Platform.runLater(() -> {
            if (ui != null && ui.getVisualisation() instanceof Visualisation vis) {
                vis.addNewCustomer(customer.getId(), customer.getType(), customer.getItems(), customer.getCurrentLocation());
            }
        });
    }

    @Override
    public void customerMoved(int customerId, ServicePointType from, ServicePointType to) {
        Platform.runLater(() -> {
            if (ui != null && ui.getVisualisation() instanceof Visualisation vis) {
                vis.moveCustomer(customerId, from, to);
            }

            if (queueSizes.containsKey(to)) {
                int newSize = queueSizes.get(to) + 1;
                queueSizes.put(to, newSize);
                if (newSize > maxQueueLength) {
                    maxQueueLength = newSize;
                }
            }

            double currentTime = Clock.getInstance().getTime();
            queueLengthSeries.getData().add(new XYChart.Data<>(currentTime, getTotalQueueSize()));
        });
    }

    @Override
    public void customerCompleted(int customerId, ServicePointType type) {
        Customer customer = activeCustomers.remove(customerId);
        if (customer != null) {
            customer.setRemovalTime(Clock.getInstance().getTime());
            customer.reportResults();

            servicePointCustomerCount.merge(type, 1, Integer::sum);
            servicePointTotalTime.merge(type, customer.getTotalTime(), Double::sum);

            updateStatistics();
        }

        Platform.runLater(() -> {
            if (ui != null && ui.getVisualisation() instanceof Visualisation vis) {
                vis.removeCustomer(customerId);
            }
            queueSizes.put(type, Math.max(0, queueSizes.getOrDefault(type, 0) - 1));
        });
    }

    private int getTotalQueueSize() {
        return queueSizes.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void updateStatistics() {
        Platform.runLater(() -> {
            totalCustomersLabel.setText(String.valueOf(Customer.getTotalCompletedCustomers()));
            avgWaitTimeLabel.setText(String.format("%.2f", Customer.getMeanServiceTime()));
            maxQueueLabel.setText(String.valueOf(maxQueueLength));

            double currentTime = Clock.getInstance().getTime();
            if (currentTime > 0) {
                updateServicePointStats(ServicePointType.ENTRANCE, entranceCustomersLabel, entranceServiceTimeLabel, entranceUtilizationLabel, currentTime);
                updateServicePointStats(ServicePointType.SHOPPING, shoppingCustomersLabel, shoppingServiceTimeLabel, shoppingUtilizationLabel, currentTime);
                updateServicePointStats(ServicePointType.REGULAR_CHECKOUT, regularCustomersLabel, regularServiceTimeLabel, regularUtilizationLabel, currentTime);
                updateServicePointStats(ServicePointType.EXPRESS_CHECKOUT, expressCustomersLabel, expressServiceTimeLabel, expressUtilizationLabel, currentTime);
            }
        });
    }

    private void updateServicePointStats(ServicePointType type, Label customers, Label avgService, Label utilization, double currentTime) {
        int count = servicePointCustomerCount.getOrDefault(type, 0);
        double totalService = servicePointTotalTime.getOrDefault(type, 0.0);

        customers.setText(String.valueOf(count));
        avgService.setText(count > 0 ? String.format("%.2f", totalService / count) : "0.00");
        utilization.setText(currentTime > 0 ? String.format("%.0f%%", (totalService / currentTime) * 100) : "0%");
    }
}
