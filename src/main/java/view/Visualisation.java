package view;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import simu.model.CustomerType;
import simu.model.ServicePointType;

import java.util.HashMap;
import java.util.Map;

// TODO: CUSTOMERS NOT MOVING TO CHECKOUT, REGULAR CUSTOMERS USING EXPRESS CHECKOUT
public class Visualisation extends Canvas implements IVisualisation {
    private GraphicsContext gc;
    private Map<ServicePointType, Rectangle2D> servicePoints;
    private Map<ServicePointType, Integer> queueSizes;
    private Map<Integer, CustomerVisual> customers;

    public Visualisation(int w, int h) {
        super(w, h);
        gc = this.getGraphicsContext2D();

        // Initialize tracking maps
        servicePoints = new HashMap<>();
        customers = new HashMap<>();
        queueSizes = new HashMap<>();

        // Set up service point boundaries
        initializeServicePoints();

        clearDisplay();
    }

    private void initializeServicePoints() {

        servicePoints.put(ServicePointType.ENTRANCE,
                new Rectangle2D(50, 80, 120, 350));

        servicePoints.put(ServicePointType.SHOPPING,
                new Rectangle2D(220, 80, 300, 350));

        servicePoints.put(ServicePointType.REGULAR_CHECKOUT,
                new Rectangle2D(550, 200, 150, 100));

        servicePoints.put(ServicePointType.EXPRESS_CHECKOUT,
                new Rectangle2D(550, 350, 150, 100));

        servicePoints.put(ServicePointType.SELF_CHECKOUT,
                new Rectangle2D(550, 50, 150, 100));
    }

    @Override
    public void clearDisplay() {
        // Fill background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Draw service points
        drawServicePoints();

        // Draw all customers
        drawCustomers();
    }

    private void drawServicePoints() {
        // Draw each service point rectangle with appropriate colors and labels
        for (Map.Entry<ServicePointType, Rectangle2D> entry : servicePoints.entrySet()) {
            ServicePointType type = entry.getKey();
            Rectangle2D rect = entry.getValue();

            // Choose color based on service point type
            switch (type) {
                case ENTRANCE:
                    gc.setFill(Color.LIGHTBLUE);
                    break;
                case SHOPPING:
                    gc.setFill(Color.LIGHTGREEN);
                    break;
                case REGULAR_CHECKOUT:
                    gc.setFill(Color.LIGHTYELLOW);
                    break;
                case EXPRESS_CHECKOUT:
                    gc.setFill(Color.LIGHTPINK);
                    break;
                case SELF_CHECKOUT:
                    gc.setFill(Color.LIGHTGRAY);
                    break;
                default:
                    gc.setFill(Color.WHITE);
            }

            // Draw filled rectangle
            gc.fillRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());

            // Draw border
            gc.setStroke(Color.BLACK);
            gc.strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());

            // Add label
            gc.setFill(Color.BLACK);
            gc.fillText(formatServicePointName(type),
                    rect.getMinX() + 10, rect.getMinY() + 20);

            // Draw queue area if applicable (for checkout points)
            if (type == ServicePointType.REGULAR_CHECKOUT ||
                    type == ServicePointType.EXPRESS_CHECKOUT ||
                    type == ServicePointType.SELF_CHECKOUT) {
                drawQueueArea(type, rect);
            }
        }
    }

    private void drawQueueArea(ServicePointType type, Rectangle2D servicePoint) {
        double queueX = servicePoint.getMinX();
        double queueY = servicePoint.getMinY() - 30;
        double queueWidth = servicePoint.getWidth();
        double queueHeight = 25;

        // Draw queue area with light color
        gc.setFill(Color.LIGHTYELLOW);
        gc.fillRect(queueX, queueY, queueWidth, queueHeight);

        // Draw border
        gc.setStroke(Color.GRAY);
        gc.setLineDashes(5, 5); // Dashed line
        gc.strokeRect(queueX, queueY, queueWidth, queueHeight);
        gc.setLineDashes(0); // Reset to solid line

        // Add queue label

		int queueSize = queueSizes.getOrDefault(type, 0);
		gc.setFill(Color.BLACK);
		gc.fillText("Queue size: " + queueSize, queueX + 5, queueY + 15);
    }

	/**
	 * Increases the queue size for a specific service point
	 * @param type The service point type whose queue should be increased
	 */
	public void incrementQueueSize(ServicePointType type) {
		int size = queueSizes.getOrDefault(type, 0);
		size++;
		queueSizes.put(type, size);
	}

	/**
	 * Decreases the queue size for a specific service point
	 * @param type The service point type whose queue should be decreased
	 */
	public void decrementQueueSize(ServicePointType type) {
		int size = queueSizes.getOrDefault(type, 0);
		if (size > 0) { // Prevent negative queue sizes
			size--;
		}
		queueSizes.put(type, size);
	}


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

    private void drawCustomers() {
        for (CustomerVisual customer : customers.values()) {
            drawCustomer(customer);
        }
    }

    private void drawCustomer(CustomerVisual customer) {
        // Draw customer circle
        gc.setFill(customer.getColor());
        gc.fillOval(customer.getX(), customer.getY(), 20, 20);

        // Draw customer ID
        gc.setFill(Color.BLACK);
        gc.fillText(Integer.toString(customer.getId()),
                customer.getX() + 6, customer.getY() + 14);

        // Draw item count for customers with items
        if (customer.getItems() > 0) {
            gc.setFill(Color.WHITE);
            gc.fillText(Integer.toString(customer.getItems()),
                    customer.getX() + 30, customer.getY() + 10);
        }
    }

    @Override
    public void newCustomer() {
        int newId = customers.size() + 1;
        addNewCustomer(newId, CustomerType.REGULAR, 15, ServicePointType.ENTRANCE);
    }


    public void addNewCustomer(int id, CustomerType type, int items, ServicePointType location) {
        CustomerVisual customer = new CustomerVisual(id, type, items);
        placeCustomerAtServicePoint(customer, location);
        customers.put(id, customer);
        clearDisplay(); // Redraw everything
    }

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

    public void moveCustomer(int customerId, ServicePointType from, ServicePointType to) {
        CustomerVisual customer = customers.get(customerId);
        if (customer != null) {
            customer.setLocation(to);
            placeCustomerAtServicePoint(customer, to);
            clearDisplay(); // Redraw everything
        }
    }

    public void removeCustomer(int customerId) {
        customers.remove(customerId);
        clearDisplay(); // Redraw everything
    }

    public void updateCustomerItems(int customerId, int items) {
        CustomerVisual customer = customers.get(customerId);
        if (customer != null) {
            customer.setItems(items);
            clearDisplay(); // Redraw everything
        }
    }

    // Customer visualization data
    private class CustomerVisual {
        private int id;
        private CustomerType type;
        private int items;
        private double x, y;
        private Color color;
        private ServicePointType location;

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

        // Getters and setters
        public int getId() {
            return id;
        }

        public CustomerType getType() {
            return type;
        }

        public int getItems() {
            return items;
        }

        public void setItems(int items) {
            this.items = items;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public Color getColor() {
            return color;
        }

        public ServicePointType getLocation() {
            return location;
        }

        public void setLocation(ServicePointType location) {
            this.location = location;
        }
    }
}