package simu.model;

import controller.IControllerMtoV;
import eduni.distributions.*;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.ArrivalProcess;
import simu.framework.Event;
import simu.data.SimulationConfig;

/**
 * Custom engine implementation for the store simulation model.
 * Manages service points, customer flow, and simulation events.
 */
public class MyEngine extends Engine {
    private final ArrivalProcess arrivalProcess;
    private final SimulationConfig config;

    /**
     * Creates a new simulation engine with the specified controller and configuration.
     *
     * @param controller The controller for communicating between model and view
     * @param config The simulation configuration parameters
     */
    public MyEngine(IControllerMtoV controller, SimulationConfig config) {
        super(controller);
        this.config = config;

        // Create service points for the store simulation
        servicePoints = new ServicePoint[5];

        // Create distributions using configuration
        ContinuousGenerator arrivalGenerator = createDistribution(
                config.getArrivalDistribution(),
                config.getArrivalParam()
        );

        arrivalProcess = new ArrivalProcess(arrivalGenerator, eventList, EventType.ARR1);

        // Entrance - quick processing
        servicePoints[0] = new ServicePoint(
                createDistribution("Negexp", 1.0),
                eventList,
                EventType.DEP1
        );

        // Shopping area - time depends on items
        servicePoints[1] = new ServicePoint(
                createDistribution(
                        config.getServiceDistribution(),
                        config.getServiceParam() * config.getShoppingMultiplier()
                ),
                eventList,
                EventType.DEP2
        );

        // Regular checkout
        servicePoints[2] = new ServicePoint(
                createDistribution(
                        config.getServiceDistribution(),
                        config.getServiceParam() * config.getRegularMultiplier()
                ),
                eventList,
                EventType.DEP3
        );

        // Express checkout
        servicePoints[3] = new ServicePoint(
                createDistribution(
                        config.getServiceDistribution(),
                        config.getServiceParam() * config.getExpressMultiplier()
                ),
                eventList,
                EventType.DEP4
        );

        // Self-checkout
        servicePoints[4] = new ServicePoint(
                createDistribution(
                        config.getServiceDistribution(),
                        config.getServiceParam() * config.getSelfCheckoutMultiplier()
                ),
                eventList,
                EventType.DEP5
        );
    }

    /**
     * Creates a distribution generator based on the specified type and parameter.
     *
     * @param type The distribution type ("Normal", "Uniform", or "Negexp")
     * @param param The primary parameter for the distribution
     * @return A continuous generator for the specified distribution
     *
     * <p>For param=30, the distributions behave as follows:</p>
     * <ul>
     *   <li><b>Negexp:</b> Mean=30. </li>
     *   <li><b>Normal:</b> Mean=30, Variance= param / 3 (10). 68% of arrivals between 26.8-33.2 time units.
     *       Creates more evenly spaced arrivals.</li>
     *   <li><b>Uniform:</b> Range= param * 0.5, param * 1.5 (15-45). Equal probability across this range.
     *       Creates moderate variability without extremes.</li>
     * </ul>
     */
    //TODO: More robust distribution creation? (Able to choose variance and range values)
    private ContinuousGenerator createDistribution(String type, double param) {
        return switch (type) {
            case "Normal" -> new Normal(param, param / 3);
            case "Uniform" -> new Uniform(param * 0.5, param * 1.5);
            default -> new Negexp(param);
        };
    }

    /**
     * Initializes the simulation by scheduling the first customer arrival.
     */
    @Override
    protected void initialization() {
        arrivalProcess.generateNext(); // Schedule the first arrival
    }

    /**
     * Processes simulation events based on their type.
     * Handles customer movement through different service points in the store.
     *
     * @param t The event to process
     */
    @Override
    protected void runEvent(Event t) {
        Customer customer;

        switch ((EventType) t.getType()) {
            case ARR1:
                customer = new Customer(config);

                // Add to first service point (entrance)
                servicePoints[0].addQueue(customer);
                controller.customerCreated(customer);
                arrivalProcess.generateNext();
                break;

            case DEP1: // Customer moves from entrance to shopping area
                customer = servicePoints[0].removeQueue();

                // Update location and notify controller
                controller.customerMoved(customer.getId(), ServicePointType.ENTRANCE, ServicePointType.SHOPPING);

                // Start shopping process
                customer.startShopping();
                servicePoints[1].addQueue(customer);
                servicePoints[1].beginService(); // To queue customers into checkout service points
                break;

            case DEP2: // Customer finishes shopping and moves to checkout

                customer = servicePoints[1].removeQueue();
                if (customer == null) { // Null check for beginService() above, otherwise crashes
                    System.out.println("Warning: No customer found in shopping area queue");
                    break;
                }
                customer.endShopping();


                // Determine which checkout to use based on customer type/items
                ServicePointType checkoutType;
                if (customer.getType() == CustomerType.EXPRESS || customer.getItems() <= 10) {
                    checkoutType = ServicePointType.EXPRESS_CHECKOUT;
                    customer.setCurrentLocation(checkoutType);
                    controller.customerMoved(customer.getId(), ServicePointType.SHOPPING, checkoutType);
                    servicePoints[3].addQueue(customer);
                } else {
                    // Regular checkout or self-checkout (random choice)
                    if (Math.random() > 0.7) { // 30% chance for self-checkout
                        checkoutType = ServicePointType.SELF_CHECKOUT;
                        customer.setCurrentLocation(checkoutType);
                        controller.customerMoved(customer.getId(), ServicePointType.SHOPPING, checkoutType);
                        servicePoints[4].addQueue(customer);
                    } else {
                        checkoutType = ServicePointType.REGULAR_CHECKOUT;
                        customer.setCurrentLocation(checkoutType);
                        controller.customerMoved(customer.getId(), ServicePointType.SHOPPING, checkoutType);
                        servicePoints[2].addQueue(customer);
                    }
                }

                customer.startCheckout();

                break;

            case DEP3:
                // Customer leaves regular checkout
                customer = servicePoints[2].removeQueue();
                customer.setRemovalTime(Clock.getInstance().getTime());
                controller.customerCompleted(customer.getId(), ServicePointType.REGULAR_CHECKOUT);
                customer.reportResults();
                break;

            case DEP4:
                // Customer leaves express checkout
                customer = servicePoints[3].removeQueue();
                customer.setRemovalTime(Clock.getInstance().getTime());
                controller.customerCompleted(customer.getId(), ServicePointType.EXPRESS_CHECKOUT);
                customer.reportResults();
                break;

            case DEP5:
                // Customer leaves self-checkout
                customer = servicePoints[4].removeQueue();
                customer.setRemovalTime(Clock.getInstance().getTime());
                controller.customerCompleted(customer.getId(), ServicePointType.SELF_CHECKOUT);
                customer.reportResults();
                break;
        }
    }

    /**
     * Reports the final simulation results.
     * Notifies the controller of the simulation end time.
     */
    @Override
    protected void results() {
        controller.showEndTime(Clock.getInstance().getTime());
    }
}