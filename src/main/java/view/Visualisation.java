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
import simu.model.ServicePointType;

import java.util.HashMap;
import java.util.Map;

// TODO: CUSTOMERS NOT MOVING TO CHECKOUT, REGULAR CUSTOMERS USING EXPRESS CHECKOUT
public class Visualisation extends Canvas implements IVisualisation {
    private GraphicsContext gc;
    private Map<ServicePointType, Rectangle2D> servicePoints;
    private Map<ServicePointType, Integer> queueSizes;
    private Map<Integer, CustomerVisual> customers;
    private VisualizationHelper helper;

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

    @Override
    public void clearDisplay() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        drawServicePoints();
        drawCustomers();
    }

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
            drawServicePointIcon(type, rect.getMinX() + PADDING, rect.getMinY() + PADDING, ICON_SIZE);

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

    // Draw icon for service point type
    private void drawServicePointIcon(ServicePointType type, double x, double y, double size) {
        gc.save();
        switch (type) {
            case ENTRANCE:
                helper.drawEntranceIcon(x, y, size);
                break;
            case SHOPPING:
                helper.drawShoppingIcon(x, y, size);
                break;
            case REGULAR_CHECKOUT:
                helper.drawRegularCheckoutIcon(x, y, size);
                break;
            case EXPRESS_CHECKOUT:
                helper.drawExpressCheckoutIcon(x, y, size);
                break;
            case SELF_CHECKOUT:
                helper.drawSelfCheckoutIcon(x, y, size);
                break;
        }
        gc.restore();
    }

    // Get occupancy level
    private int getOccupancyLevel(ServicePointType type) {
        // 0 = free, 1 = busy, 2 = full
        return 1;
    }

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

    public void incrementQueueSize(ServicePointType type) {
        int size = queueSizes.getOrDefault(type, 0);
        size++;
        queueSizes.put(type, size);
        clearDisplay();
    }

    public void decrementQueueSize(ServicePointType type) {
        int size = queueSizes.getOrDefault(type, 0);
        if (size > 0) {
            size--;
        }
        queueSizes.put(type, size);
        clearDisplay();
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
        // Constants for better readability and easier adjustments
        final int IMAGE_SIZE = 40;
        final int ID_BADGE_SIZE = 16;
        final int ITEM_BADGE_SIZE = 16;
        final int BADGE_OFFSET = 4;
        final int TEXT_OFFSET_Y = 10;

        double x = customer.getX();
        double y = customer.getY();

        // Draw customer image
        Image customerImage = new Image("customer-4.png");
        gc.drawImage(customerImage, x, y, IMAGE_SIZE, IMAGE_SIZE);

        // Draw ID badge (circular background)
        gc.setFill(Color.WHITE);
        gc.fillOval(x - ID_BADGE_SIZE / 2, y - ID_BADGE_SIZE / 2, ID_BADGE_SIZE, ID_BADGE_SIZE);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(x - ID_BADGE_SIZE / 2, y - ID_BADGE_SIZE / 2, ID_BADGE_SIZE, ID_BADGE_SIZE);

        // Draw ID number in badge
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.fillText(Integer.toString(customer.getId()),
                x, y + BADGE_OFFSET);

        // Draw item count badge for customers with items
        if (customer.getItems() > 0) {
            // Position the items badge in the bottom right corner of the image
            double itemBadgeX = x + IMAGE_SIZE - ITEM_BADGE_SIZE;
            double itemBadgeY = y + IMAGE_SIZE - ITEM_BADGE_SIZE;

            // Draw square background for item count (blue)
            gc.setFill(Color.ROYALBLUE);
            gc.fillRect(itemBadgeX, itemBadgeY, ITEM_BADGE_SIZE, ITEM_BADGE_SIZE);
            gc.setStroke(Color.WHITE);
            gc.strokeRect(itemBadgeX, itemBadgeY, ITEM_BADGE_SIZE, ITEM_BADGE_SIZE);

            // Draw item count number (white text)
            gc.setFill(Color.WHITE);
            gc.fillText(Integer.toString(customer.getItems()),
                    itemBadgeX + ITEM_BADGE_SIZE / 2,
                    itemBadgeY + ITEM_BADGE_SIZE / 2 + BADGE_OFFSET);
        }

        // Reset text alignment for other drawing operations
        gc.setTextAlign(TextAlignment.LEFT);
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
            clearDisplay();
        }
    }

    public void removeCustomer(int customerId) {
        customers.remove(customerId);
        clearDisplay();
    }

    public void updateCustomerItems(int customerId, int items) {
        CustomerVisual customer = customers.get(customerId);
        if (customer != null) {
            customer.setItems(items);
            clearDisplay();
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