package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import simu.framework.Clock;
import simu.framework.IEngine;
import simu.model.*;
import view.ISimulatorUI;
import view.Visualisation;
import simu.data.SimulationConfig;

import java.util.HashMap;
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

    // FXML Statistics components
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

    private Map<Integer, Customer> activeCustomers = new ConcurrentHashMap<>();
    private Map<ServicePointType, Integer> queueSizes = new ConcurrentHashMap<>();
    private Map<ServicePointType, Integer> servicePointCustomerCount = new HashMap<>();
    private Map<ServicePointType, Double> servicePointServiceTime = new HashMap<>();

    private XYChart.Series<Number, Number> queueLengthSeries;

    private int maxQueueLength = 0;

    @FXML
    public void initialize() {
        for (ServicePointType type : ServicePointType.values()) {
            queueSizes.put(type, 0);
            servicePointCustomerCount.put(type, 0);
            servicePointServiceTime.put(type, 0.0);
        }
        initializeConfigControls();

        queueLengthSeries = new XYChart.Series<>();
        queueLengthSeries.setName("Queue Length");
        queueLengthChart.getData().add(queueLengthSeries);
    }

    private void initializeConfigControls() {
        arrivalDistributionCombo.getItems().addAll("Negexp", "Normal", "Uniform");
        serviceDistributionCombo.getItems().addAll("Normal", "Negexp", "Uniform");

        arrivalDistributionCombo.setValue(config.getArrivalDistribution());
        serviceDistributionCombo.setValue(config.getServiceDistribution());

        arrivalDistributionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) config.setArrivalDistribution(newVal);
        });

        arrivalParamField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                if (value > 0) config.setArrivalParam(value);
            } catch (NumberFormatException e) {
                arrivalParamField.setText(oldVal);
            }
        });

        expressCustomerSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double percentage = newVal.doubleValue();
            expressPercentLabel.setText(String.format("%.0f%%", percentage));
            config.setExpressCustomerPercentage(percentage);
        });

        serviceDistributionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) config.setServiceDistribution(newVal);
        });

        IntegerFieldController.setupPairedFields(minRegularItems, maxRegularItems, 11, 50,
                config::setMinRegularItems, config::setMaxRegularItems);

        IntegerFieldController.setupPairedFields(minExpressItems, maxExpressItems, 1, 10,
                config::setMinExpressItems, config::setMaxExpressItems);

        DoubleFieldController.setupField(serviceParamField, 0.1, 100, config::setServiceParam);
        DoubleFieldController.setupField(shoppingMultiplier, 0.1, 10, config::setShoppingMultiplier);
        DoubleFieldController.setupField(regularMultiplier, 0.1, 10, config::setRegularMultiplier);
        DoubleFieldController.setupField(expressMultiplier, 0.1, 10, config::setExpressMultiplier);
        DoubleFieldController.setupField(selfCheckoutMultiplier, 0.1, 10, config::setSelfCheckoutMultiplier);
    }

    public void setUI(ISimulatorUI ui) {
        this.ui = ui;
    }

    @FXML
    public void startSimulation() {
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
            engine.setDelay((long) (engine.getDelay() * 1.10));
        }
    }

    @FXML
    public void increaseSpeed() {
        if (engine != null && !paused) {
            engine.setDelay((long) (engine.getDelay() * 0.9));
        }
    }

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
        showAlert(Alert.AlertType.INFORMATION, "Load Configuration", "This feature will be implemented");
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

    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> resultsLabel.setText(String.format("%.2f", time)));
    }

    @Override
    public void customerCreated(Customer customer) {
        activeCustomers.put(customer.getId(), customer);
        System.out.println("Controller tracking customer #" + customer.getId());

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

                queueSizes.merge(to, 1, Integer::sum);
                queueSizes.merge(from, -1, (oldValue, value) -> Math.max(0, oldValue + value));

                int totalQueue = totalQueueLength();
                if (totalQueue > maxQueueLength) {
                    maxQueueLength = totalQueue;
                }

                double currentTime = Clock.getInstance().getTime();
                queueLengthSeries.getData().add(new XYChart.Data<>(currentTime, totalQueue));
            }
        });
    }

    @Override
    public void customerCompleted(int customerId, ServicePointType type) {
        Customer customer = activeCustomers.remove(customerId);
        if (customer != null) {
            customer.setRemovalTime(Clock.getInstance().getTime());
            customer.reportResults();

            servicePointCustomerCount.put(type, servicePointCustomerCount.get(type) + 1);
            servicePointServiceTime.put(type, servicePointServiceTime.get(type) + customer.getCheckoutDuration());

            updateStatistics();
        }

        Platform.runLater(() -> {
            if (ui != null && ui.getVisualisation() instanceof Visualisation vis) {
                vis.removeCustomer(customerId);
                queueSizes.merge(type, -1, (oldValue, value) -> Math.max(0, oldValue + value));
            }
        });
    }

    private int totalQueueLength() {
        return queueSizes.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void updateStatistics() {
        Platform.runLater(() -> {
            totalCustomersLabel.setText(String.valueOf(Customer.getTotalCompletedCustomers()));
            avgWaitTimeLabel.setText(String.format("%.2f", Customer.getMeanServiceTime()));
            maxQueueLabel.setText(String.valueOf(maxQueueLength));

            updateServicePointStats(ServicePointType.ENTRANCE, entranceCustomersLabel, entranceServiceTimeLabel, entranceUtilizationLabel);
            updateServicePointStats(ServicePointType.SHOPPING, shoppingCustomersLabel, shoppingServiceTimeLabel, shoppingUtilizationLabel);
            updateServicePointStats(ServicePointType.REGULAR_CHECKOUT, regularCustomersLabel, regularServiceTimeLabel, regularUtilizationLabel);
            updateServicePointStats(ServicePointType.EXPRESS_CHECKOUT, expressCustomersLabel, expressServiceTimeLabel, expressUtilizationLabel);
        });
    }

    private void updateServicePointStats(ServicePointType type, Label customers, Label avgService, Label utilization) {
        int served = servicePointCustomerCount.get(type);
        double totalServiceTime = servicePointServiceTime.get(type);
        double totalTime = Clock.getInstance().getTime();

        customers.setText(String.valueOf(served));
        avgService.setText(served > 0 ? String.format("%.2f", totalServiceTime / served) : "0.00");
        utilization.setText(totalTime > 0 ? String.format("%.0f%%", (totalServiceTime / totalTime) * 100) : "0%");
    }
}
