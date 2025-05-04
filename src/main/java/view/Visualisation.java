package view;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import simu.model.CustomerType;
import simu.model.CustomerVisual;
import simu.model.ServicePointType;

import java.util.HashMap;
import java.util.Map;

/**
 * Canvas-based visualization for the store simulation.
 * Responsible for rendering the simulation state, including service points
 * and customers, and handling customer movement through the system.
 */
public class Visualisation extends Canvas implements IVisualisation {
    private GraphicsContext gc;
    private VisualizationHelper helper;
    private Map<ServicePointType, Rectangle2D> servicePoints;
    private Map<ServicePointType, Integer> queueSizes;
    private Map<Integer, CustomerVisual> customers;

    /**
     * Creates a new visualization canvas with the specified dimensions.
     * Initializes the helper classes and data structures for tracking simulation elements.
     *
     * @param w The width of the canvas
     * @param h The height of the canvas
     */
    public Visualisation(int w, int h) {
        super(w, h);
        gc = this.getGraphicsContext2D();
        this.helper = new VisualizationHelper(gc);
        // Initialize tracking maps
        servicePoints = new HashMap<>();
        customers = new HashMap<>();
        queueSizes = new HashMap<>();

        // Set up service point boundaries
        initializeServicePoints();

        clearDisplay();
    }

    /**
     * Initializes the service point areas on the canvas.
     * Defines the boundaries for each service point type.
     */
    private void initializeServicePoints() {
        servicePoints.put(ServicePointType.ENTRANCE,
                new Rectangle2D(50, 80, 120, 350));

        servicePoints.put(ServicePointType.SHOPPING,
                new Rectangle2D(220, 80, 300, 350));

        servicePoints.put(ServicePointType.REGULAR_CHECKOUT,
                new Rectangle2D(550, 200, 200, 100));

        servicePoints.put(ServicePointType.EXPRESS_CHECKOUT,
                new Rectangle2D(550, 350, 200, 100));

        servicePoints.put(ServicePointType.SELF_CHECKOUT,
                new Rectangle2D(550, 50, 200, 100));
    }

    /**
     * Clears the display and redraws all simulation elements.
     * Fills the canvas with a white background and renders service points and customers.
     */
    @Override
    public void clearDisplay() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        drawServicePoints();
        drawCustomers();
    }

    /**
     * Draws all service points on the canvas.
     * Renders each service point with its appropriate color, icon, and queue area.
     */
    private void drawServicePoints() {
        // Constants for visual styling
        final int CORNER_RADIUS = 8;
        final int ICON_SIZE = 24;
        final int PADDING = 10;
        final Font TITLE_FONT = Font.font("Arial", FontWeight.BOLD, 14);
        final Font LABEL_FONT = Font.font("Arial", FontWeight.NORMAL, 12);

        for (Map.Entry<ServicePointType, Rectangle2D> entry : servicePoints.entrySet()) {
            ServicePointType type = entry.getKey();
            Rectangle2D rect = entry.getValue();

            // Background with rounded corners
            gc.setFill(helper.getServicePointColor(type));
            gc.fillRoundRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), CORNER_RADIUS, CORNER_RADIUS);

            // Draw appropriate icon based on service point type
            helper.drawServicePointIcon(type, rect.getMinX() + PADDING, rect.getMinY() + PADDING, ICON_SIZE);

            // Label with better typography
            gc.setFont(TITLE_FONT);
            gc.setFill(Color.BLACK);
            gc.fillText(formatServicePointName(type),
                    rect.getMinX() + PADDING + ICON_SIZE + PADDING,
                    rect.getMinY() + PADDING + ICON_SIZE / 2 + 5);

            // Draw queue area with improved visuals (for checkout points)
            if (type == ServicePointType.REGULAR_CHECKOUT ||
                    type == ServicePointType.EXPRESS_CHECKOUT ||
                    type == ServicePointType.SELF_CHECKOUT) {
                drawQueueArea(type, rect);
            }
        }
    }

    /**
     * Gets the occupancy level of a service point.
     *
     * @param type The service point type
     * @return The occupancy level (0 = free, 1 = busy, 2 = full)
     */
    private int getOccupancyLevel(ServicePointType type) {
        // 0 = free, 1 = busy, 2 = full
        return 1;
    }

    /**
     * Draws the queue area for a service point.
     * Shows the queue size and visual indicators for waiting customers.
     *
     * @param type The service point type
     * @param servicePoint The rectangle defining the service point area
     */
    private void drawQueueArea(ServicePointType type, Rectangle2D servicePoint) {
        // Constants for visual consistency
        final int CORNER_RADIUS = 6;
        final int PADDING = 5;
        final Font QUEUE_LABEL_FONT = Font.font("Arial", FontWeight.NORMAL, 11);
        final Font QUEUE_COUNT_FONT = Font.font("Arial", FontWeight.BOLD, 12);

        double queueX = servicePoint.getMinX();
        double queueY = servicePoint.getMinY() - 30;
        double queueWidth = servicePoint.getWidth();
        double queueHeight = 25;

        // Get queue size and determine color based on size
        int queueSize = queueSizes.getOrDefault(type, 0);
        Color queueColor = VisualizationHelper.getQueueColorBySize(queueSize);

        // Draw queue area with rounded corners
        gc.setFill(queueColor);
        gc.fillRoundRect(queueX, queueY, queueWidth, queueHeight, CORNER_RADIUS, CORNER_RADIUS);

        // Draw subtle border
        gc.setStroke(Color.gray(0.5, 0.5));
        gc.setLineWidth(1);
        gc.strokeRoundRect(queueX, queueY, queueWidth, queueHeight, CORNER_RADIUS, CORNER_RADIUS);

        // Draw queue persons indicator (small icons representing people)
        helper.drawQueuePersonsIndicator(queueX + PADDING, queueY + PADDING, queueSize, type);

        // Draw queue size text
        gc.setFill(Color.BLACK);
        gc.setFont(QUEUE_LABEL_FONT);
        gc.fillText("Queue: ", queueX + queueWidth - 75, queueY + 16);

        gc.setFont(QUEUE_COUNT_FONT);
        gc.fillText(Integer.toString(queueSize), queueX + queueWidth - 35, queueY + 16);

        // Draw a type-specific indicator if relevant
        if (type == ServicePointType.EXPRESS_CHECKOUT) {
            helper.drawMaxItemsIndicator(queueX + 5, queueY + 16);
        }
    }

    /**
     * Increments the queue size for a service point and updates the display.
     *
     * @param type The service point type whose queue size should be incremented
     */
    public void incrementQueueSize(ServicePointType type) {
        int size = queueSizes.getOrDefault(type, 0);
        size++;
        queueSizes.put(type, size);
        clearDisplay();
    }

    /**
     * Decrements the queue size for a service point and updates the display.
     *
     * @param type The service point type whose queue size should be decremented
     */
    public void decrementQueueSize(ServicePointType type) {
        int size = queueSizes.getOrDefault(type, 0);
        if (size > 0) {
            size--;
        }
        queueSizes.put(type, size);
        clearDisplay();
    }

    /**
     * Formats a service point type name for display.
     * Converts from enum format (SNAKE_CASE) to Title Case with spaces.
     *
     * @param type The service point type to format
     * @return The formatted name
     */
    private String formatServicePointName(ServicePointType type) {
        String name = type.toString();
        String[] words = name.split("_");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append(word.charAt(0)).append(word.substring(1).toLowerCase());
        }

        return formatted.toString();
    }

    /**
     * Draws all customers on the canvas.
     */
    private void drawCustomers() {
        for (CustomerVisual customer : customers.values()) {
            helper.drawCustomer(customer);
        }
    }

    /**
     * Creates a new customer in the visualization.
     * Legacy method implementation from IVisualisation interface.
     */
    @Override
    public void newCustomer() {
        int newId = customers.size() + 1;
        addNewCustomer(newId, CustomerType.REGULAR, 15, ServicePointType.ENTRANCE);
    }

    /**
     * Adds a new customer to the visualization.
     *
     * @param id The customer's unique ID
     * @param type The customer type (regular or express)
     * @param items The number of items the customer is purchasing
     * @param location The initial service point location of the customer
     */
    public void addNewCustomer(int id, CustomerType type, int items, ServicePointType location) {
        CustomerVisual customer = new CustomerVisual(id, type, items);
        placeCustomerAtServicePoint(customer, location);
        customers.put(id, customer);
        clearDisplay(); // Redraw everything
    }

    /**
     * Places a customer at a specified service point location.
     * Positions the customer randomly within the service point area.
     *
     * @param customer The customer visual to place
     * @param location The service point location to place the customer at
     */
    private void placeCustomerAtServicePoint(CustomerVisual customer, ServicePointType location) {
        Rectangle2D rect = servicePoints.get(location);
        if (rect == null) return;

        // Place at random position within the service point
        double padding = 10; // Keep away from edges
        double x = rect.getMinX() + padding + Math.random() * (rect.getWidth() - 40);
        double y = rect.getMinY() + padding + Math.random() * (rect.getHeight() - 40);

        customer.setX(x);
        customer.setY(y);
        customer.setLocation(location);
    }

    /**
     * Moves a customer from one service point to another.
     *
     * @param customerId The ID of the customer to move
     * @param from The service point the customer is moving from
     * @param to The service point the customer is moving to
     */
    public void moveCustomer(int customerId, ServicePointType from, ServicePointType to) {
        CustomerVisual customer = customers.get(customerId);
        if (customer != null) {
            customer.setLocation(to);
            placeCustomerAtServicePoint(customer, to);
            clearDisplay();
        }
    }

    /**
     * Removes a customer from the visualization.
     *
     * @param customerId The ID of the customer to remove
     */
    public void removeCustomer(int customerId) {
        customers.remove(customerId);
        clearDisplay();
    }

    /**
     * Updates the number of items a customer has.
     *
     * @param customerId The ID of the customer to update
     * @param items The new item count
     */
    public void updateCustomerItems(int customerId, int items) {
        CustomerVisual customer = customers.get(customerId);
        if (customer != null) {
            customer.setItems(items);
            clearDisplay();
        }
    }
}