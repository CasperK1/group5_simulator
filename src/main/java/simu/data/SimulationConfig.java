package simu.data;

public class SimulationConfig {
    // Arrival configuration
    private String arrivalDistribution = "Negexp";
    private double arrivalParam = 5.0;

    // Customer parameters
    private double expressCustomerPercentage = 20.0;
    private int minRegularItems = 10;
    private int maxRegularItems = 30;
    private int minExpressItems = 1;
    private int maxExpressItems = 10;

    // Service point parameters
    private String serviceDistribution = "Normal";
    private double serviceParam = 8.0;
    private double shoppingMultiplier = 1;
    private double regularMultiplier = 1.0;
    private double expressMultiplier = 0.7;
    private double selfCheckoutMultiplier = 1.2;

    // Getters and setters

    /**
     * Gets the type of distribution used for customer arrivals.
     *
     * @return The arrival distribution type ("Negexp", "Normal", or "Uniform")
     */
    public String getArrivalDistribution() {
        return arrivalDistribution;
    }

    /**
     * Sets the type of distribution used for customer arrivals.
     *
     * @param arrivalDistribution The arrival distribution type ("Negexp", "Normal", or "Uniform")
     */
    public void setArrivalDistribution(String arrivalDistribution) {
        this.arrivalDistribution = arrivalDistribution;
    }

    /**
     * Gets the parameter controlling customer arrival times.
     * For Negexp: mean time between arrivals.
     * For Normal: mean (variance calculated as param/3).
     * For Uniform: mid-point (range is param*0.5 to param*1.5).
     *
     * @return The arrival parameter value
     */
    public double getArrivalParam() {
        return arrivalParam;
    }

    /**
     * Sets the parameter controlling customer arrival times.
     *
     * @param arrivalParam The arrival parameter value
     */
    public void setArrivalParam(double arrivalParam) {
        this.arrivalParam = arrivalParam;
    }

    /**
     * Gets the percentage of customers that are express customers (1-10 items).
     *
     * @return The percentage of express customers (0-100)
     */
    public double getExpressCustomerPercentage() {
        return expressCustomerPercentage;
    }

    /**
     * Sets the percentage of customers that are express customers.
     *
     * @param expressCustomerPercentage The percentage of express customers (0-100)
     */
    public void setExpressCustomerPercentage(double expressCustomerPercentage) {
        this.expressCustomerPercentage = expressCustomerPercentage;
    }

    /**
     * Gets the type of distribution used for service times.
     *
     * @return The service distribution type ("Normal", "Negexp", or "Uniform")
     */
    public String getServiceDistribution() {
        return serviceDistribution;
    }

    /**
     * Sets the type of distribution used for service times.
     *
     * @param serviceDistribution The service distribution type ("Normal", "Negexp", or "Uniform")
     */
    public void setServiceDistribution(String serviceDistribution) {
        this.serviceDistribution = serviceDistribution;
    }

    /**
     * Gets the minimum number of items for regular customers.
     *
     * @return The minimum items for regular customers
     */
    public int getMinRegularItems() {
        return minRegularItems;
    }

    /**
     * Sets the minimum number of items for regular customers.
     *
     * @param minRegularItems The minimum items for regular customers
     */
    public void setMinRegularItems(int minRegularItems) {
        this.minRegularItems = minRegularItems;
    }

    /**
     * Gets the maximum number of items for regular customers.
     *
     * @return The maximum items for regular customers
     */
    public int getMaxRegularItems() {
        return maxRegularItems;
    }

    /**
     * Sets the maximum number of items for regular customers.
     *
     * @param maxRegularItems The maximum items for regular customers
     */
    public void setMaxRegularItems(int maxRegularItems) {
        this.maxRegularItems = maxRegularItems;
    }

    /**
     * Gets the minimum number of items for express customers.
     *
     * @return The minimum items for express customers
     */
    public int getMinExpressItems() {
        return minExpressItems;
    }

    /**
     * Sets the minimum number of items for express customers.
     *
     * @param minExpressItems The minimum items for express customers
     */
    public void setMinExpressItems(int minExpressItems) {
        this.minExpressItems = minExpressItems;
    }

    /**
     * Gets the maximum number of items for express customers.
     *
     * @return The maximum items for express customers
     */
    public int getMaxExpressItems() {
        return maxExpressItems;
    }

    /**
     * Sets the maximum number of items for express customers.
     *
     * @param maxExpressItems The maximum items for express customers
     */
    public void setMaxExpressItems(int maxExpressItems) {
        this.maxExpressItems = maxExpressItems;
    }

    /**
     * Gets the base parameter for service time distributions.
     * For Normal: mean (variance calculated as param/3).
     * For Negexp: mean time.
     * For Uniform: mid-point (range is param*0.5 to param*1.5).
     *
     * @return The service parameter value
     */
    public double getServiceParam() {
        return serviceParam;
    }

    /**
     * Sets the base parameter for service time distributions.
     *
     * @param serviceParam The service parameter value
     */
    public void setServiceParam(double serviceParam) {
        this.serviceParam = serviceParam;
    }

    /**
     * Gets the multiplier applied to shopping area service time.
     * Higher values result in longer shopping times.
     *
     * @return The shopping time multiplier
     */
    public double getShoppingMultiplier() {
        return shoppingMultiplier;
    }

    /**
     * Sets the multiplier applied to shopping area service time.
     *
     * @param shoppingMultiplier The shopping time multiplier
     */
    public void setShoppingMultiplier(double shoppingMultiplier) {
        this.shoppingMultiplier = shoppingMultiplier;
    }

    /**
     * Gets the multiplier applied to regular checkout service time.
     *
     * @return The regular checkout time multiplier
     */
    public double getRegularMultiplier() {
        return regularMultiplier;
    }

    /**
     * Sets the multiplier applied to regular checkout service time.
     *
     * @param regularMultiplier The regular checkout time multiplier
     */
    public void setRegularMultiplier(double regularMultiplier) {
        this.regularMultiplier = regularMultiplier;
    }

    /**
     * Gets the multiplier applied to express checkout service time.
     * Lower values result in faster express checkouts.
     *
     * @return The express checkout time multiplier
     */
    public double getExpressMultiplier() {
        return expressMultiplier;
    }

    /**
     * Sets the multiplier applied to express checkout service time.
     *
     * @param expressMultiplier The express checkout time multiplier
     */
    public void setExpressMultiplier(double expressMultiplier) {
        this.expressMultiplier = expressMultiplier;
    }

    /**
     * Gets the multiplier applied to self-checkout service time.
     * Higher values indicate longer self-checkout times.
     *
     * @return The self-checkout time multiplier
     */
    public double getSelfCheckoutMultiplier() {
        return selfCheckoutMultiplier;
    }

    /**
     * Sets the multiplier applied to self-checkout service time.
     *
     * @param selfCheckoutMultiplier The self-checkout time multiplier
     */
    public void setSelfCheckoutMultiplier(double selfCheckoutMultiplier) {
        this.selfCheckoutMultiplier = selfCheckoutMultiplier;
    }

    public void reset() {
        // Reset arrival configuration
        this.arrivalDistribution = "Negexp";
        this.arrivalParam = 5.0;

        // Reset customer parameters
        this.expressCustomerPercentage = 20.0;
        this.minRegularItems = 10;
        this.maxRegularItems = 30;
        this.minExpressItems = 1;
        this.maxExpressItems = 10;

        // Reset service point parameters
        this.serviceDistribution = "Normal";
        this.serviceParam = 8.0;
        this.shoppingMultiplier = 1;
        this.regularMultiplier = 1.0;
        this.expressMultiplier = 0.7;
        this.selfCheckoutMultiplier = 1.2;
    }

}