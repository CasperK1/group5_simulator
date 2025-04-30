package simu.model;

import javafx.scene.paint.Color;

/**
 * Represents the visual representation of a customer in the simulation.
 * Contains properties needed for rendering a customer on the canvas,
 * including position, color, and identifying information.
 */
public class CustomerVisual {
    private int id;
    private CustomerType type;
    private int items;
    private double x, y;
    private Color color;
    private ServicePointType location;

    /**
     * Creates a new visual representation of a customer.
     * Assigns a color based on the customer type - red for express customers,
     * blue for regular customers.
     *
     * @param id The unique identifier of the customer
     * @param type The type of customer (regular or express)
     * @param items The number of items the customer is purchasing
     */
    public CustomerVisual(int id, CustomerType type, int items) {
        this.id = id;
        this.type = type;
        this.items = items;

        // Assign color based on customer type
        if (type == CustomerType.EXPRESS) {
            this.color = Color.rgb(255, 100, 100); // Red for express
        } else {
            this.color = Color.rgb(100, 100, 255); // Blue for regular
        }
    }

    /**
     * Gets the customer's unique ID.
     *
     * @return The customer ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the customer type.
     *
     * @return The customer type (regular or express)
     */
    public CustomerType getType() {
        return type;
    }

    /**
     * Gets the number of items the customer is purchasing.
     *
     * @return The item count
     */
    public int getItems() {
        return items;
    }

    /**
     * Sets the number of items the customer is purchasing.
     *
     * @param items The new item count
     */
    public void setItems(int items) {
        this.items = items;
    }

    /**
     * Gets the x-coordinate of the customer's position.
     *
     * @return The x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the customer's position.
     *
     * @param x The new x-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate of the customer's position.
     *
     * @return The y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the customer's position.
     *
     * @param y The new y-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the color used to represent the customer.
     *
     * @return The customer's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the current service point location of the customer.
     *
     * @return The service point location
     */
    public ServicePointType getLocation() {
        return location;
    }

    /**
     * Sets the current service point location of the customer.
     *
     * @param location The new service point location
     */
    public void setLocation(ServicePointType location) {
        this.location = location;
    }
}