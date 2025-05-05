package simu.model;

import simu.data.SimulationConfig;
import simu.framework.Clock;
import simu.framework.Trace;
import utils.CSVReport;

/**
 * Represents a customer in the store simulation.
 * Customers have a type (regular or express) and a number of items they are purchasing.
 */
public class Customer {
	private double arrivalTime;
	private double removalTime;
	private int id;
	private CustomerType type;
	private int items;
	private ServicePointType currentLocation;
	private ServicePointType previousLocation;
	private SimulationConfig config;

	// Statistics tracking
	private static int i = 1;
	private static long totalTime = 0;
	private static int totalCustomers = 0;

	// Timestamps for tracking service points
	private double entranceTime;
	private double shoppingStartTime;
	private double shoppingEndTime;
	private double checkoutStartTime;

	/**
	 * Creates a new customer with a unique ID and records arrival time.
	 */
	public Customer(SimulationConfig cfg) {
		id = i++;
		arrivalTime = Clock.getInstance().getTime();
		entranceTime = arrivalTime;
		config = cfg;
		if (config.getExpressCustomerPercentage() <= 0) {
			type = CustomerType.REGULAR;
		} else {
			boolean isExpress = Math.random() * 100 < config.getExpressCustomerPercentage();
			type = isExpress ? CustomerType.EXPRESS : CustomerType.REGULAR;
		}

		if (type == CustomerType.EXPRESS) {
			items = config.getMinExpressItems() +
					(int) (Math.random() * (config.getMaxExpressItems() - config.getMinExpressItems() + 1));
		} else {
			items = config.getMinRegularItems() +
					(int) (Math.random() * (config.getMaxRegularItems() - config.getMinRegularItems() + 1));
		}

		// Initial location
		currentLocation = ServicePointType.ENTRANCE;

		Trace.out(Trace.Level.INFO, "New customer #" + id + " (" + type + ") with " + items +
				" items arrived at " + arrivalTime);
	}
	public ServicePointType getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(ServicePointType location) {
		this.previousLocation = this.currentLocation;
		this.currentLocation = location;
	}

	public ServicePointType getPreviousLocation() {
		return previousLocation;
	}

	/**
	 * Gets the time when the customer was removed from the system.
	 * @return The removal time.
	 */
	public double getRemovalTime() {
		return removalTime;
	}

	/**
	 * Sets the time when the customer was removed from the system.
	 * @param removalTime The removal time to set.
	 */
	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	/**
	 * Gets the time when the customer arrived in the system.
	 * @return The arrival time.
	 */
		public double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Sets the time when the customer arrived in the system.
	 * @param arrivalTime The arrival time to set.
	 */
	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * Gets the customer's unique ID.
	 * @return The customer ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the customer type (regular or express).
	 * @return The customer type.
	 */
	public CustomerType getType() {
		return type;
	}

	/**
	 * Sets the customer type.
	 * @param type The customer type to set.
	 */
	public void setType(CustomerType type) {
		this.type = type;
	}

	/**
	 * Gets the number of items the customer is purchasing.
	 * @return The item count.
	 */
	public int getItems() {
		return items;
	}

	/**
	 * Sets the number of items the customer is purchasing.
	 * @param items The item count to set.
	 */
	public void setItems(int items) {
		this.items = items;
	}

	/**
	 * Records the time when shopping started.
	 */
	public void startShopping() {
		shoppingStartTime = Clock.getInstance().getTime();
	}

	/**
	 * Records the time when shopping ended.
	 */
	public void endShopping() {
		shoppingEndTime = Clock.getInstance().getTime();
	}

	/**
	 * Records the time when checkout started.
	 */
	public void startCheckout() {
		checkoutStartTime = Clock.getInstance().getTime();
	}

	/**
	 * Calculates how long the customer spent shopping.
	 * @return The shopping duration.
	 */
	public double getShoppingDuration() {
		if (shoppingEndTime > 0 && shoppingStartTime > 0) {
			return shoppingEndTime - shoppingStartTime;
		}
		return 0;
	}

	/**
	 * Calculates how long the customer spent at checkout.
	 * @return The checkout duration.
	 */
	public double getCheckoutDuration() {
		if (removalTime > 0 && checkoutStartTime > 0) {
			return removalTime - checkoutStartTime;
		}
		return 0;
	}

	/**
	 * Calculates the total time the customer spent in the system.
	 * @return The total time in the system.
	 */
	public double getTotalTime() {
		return removalTime - arrivalTime;
	}

	/**
	 * Reports customer statistics when they leave the system.
	 */
	public void reportResults() {
		Trace.out(Trace.Level.INFO, "\nCustomer " + id + " ready! ");
		Trace.out(Trace.Level.INFO, "Customer " + id + " arrived: " + arrivalTime);
		Trace.out(Trace.Level.INFO, "Customer " + id + " removed: " + removalTime);
		Trace.out(Trace.Level.INFO, "Customer " + id + " stayed: " + (removalTime - arrivalTime));
		Trace.out(Trace.Level.INFO, "Customer " + id + " type: " + type);
		Trace.out(Trace.Level.INFO, "Customer " + id + " items: " + items);

		// Update overall statistics
		totalTime += (removalTime - arrivalTime);
		totalCustomers++;
		double mean = totalTime / totalCustomers;

		// Save to CSV
		CSVReport.save(this, mean);

		System.out.println("Customer #" + id + " (" + type + ") with " + items +
				" items completed in " + String.format("%.2f", (removalTime - arrivalTime)) +
				" time units");
		System.out.println("Current mean customer service time: " + String.format("%.2f", mean));
	}

	/**
	 * Gets the current mean service time for all customers.
	 * @return The mean service time.
	 */
	public static double getMeanServiceTime() {
		if (totalCustomers > 0) {
			return totalTime / totalCustomers;
		}
		return 0;
	}

	/**
	 * Gets the total number of customers that have completed service.
	 * @return The total completed customers.
	 */
	public static int getTotalCompletedCustomers() {
		return totalCustomers;
	}

	/**
	 * Resets the static customer counters and statistics.
	 * Should be called before starting a new simulation.
	 */
	public static void resetStatistics() {
		i = 1;
		totalTime = 0;
		totalCustomers = 0;
	}
}