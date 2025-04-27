package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import simu.model.CustomerVisual;
import simu.model.ServicePointType;

public class VisualizationHelper {
    private GraphicsContext gc;

    public VisualizationHelper(GraphicsContext gc) {
        this.gc = gc;
    }

//COLOR SET----------------------------------------------------------

    // Helper method to get appropriate colors for service point types
    public static Color getServicePointColor(ServicePointType type) {
        switch (type) {
            case ENTRANCE:
                return Color.rgb(173, 216, 230, 0.7); // Lighter blue with transparency
            case SHOPPING:
                return Color.rgb(144, 238, 144, 0.7); // Lighter green with transparency
            case REGULAR_CHECKOUT:
                return Color.rgb(250, 250, 210, 0.7); // Lighter yellow with transparency
            case EXPRESS_CHECKOUT:
                return Color.rgb(255, 182, 193, 0.7); // Lighter pink with transparency
            case SELF_CHECKOUT:
                return Color.rgb(211, 211, 211, 0.7); // Lighter gray with transparency
            default:
                return Color.WHITE;
        }
    }

    // Get appropriate color based on queue size
    public static Color getQueueColorBySize(int queueSize) {
        if (queueSize == 0) {
            return Color.rgb(220, 255, 220, 0.7); // Light green (empty)
        } else if (queueSize <= 3) {
            return Color.rgb(255, 255, 220, 0.7); // Light yellow (short queue)
        } else if (queueSize <= 7) {
            return Color.rgb(255, 230, 200, 0.7); // Light orange (medium queue)
        } else {
            return Color.rgb(255, 220, 220, 0.7); // Light red (long queue)
        }
    }

//ICON----------------------------------------------------------
    // Draw icon for service point type
    void drawServicePointIcon(ServicePointType type, double x, double y, double size) {
        gc.save();
        switch (type) {
            case ENTRANCE:
                drawEntranceIcon(x, y, size);
                break;
            case SHOPPING:
                drawShoppingIcon(x, y, size);
                break;
            case REGULAR_CHECKOUT:
                drawRegularCheckoutIcon(x, y, size);
                break;
            case EXPRESS_CHECKOUT:
                drawExpressCheckoutIcon(x, y, size);
                break;
            case SELF_CHECKOUT:
                drawSelfCheckoutIcon(x, y, size);
                break;
        }
        gc.restore();
    }

    // Draw entrance icon (door)
    public void drawEntranceIcon(double x, double y, double size) {
        gc.setFill(Color.STEELBLUE);
        gc.fillRect(x, y, size * 0.8, size);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, size * 0.8, size);

        // Door handle
        gc.setFill(Color.BLACK);
        gc.fillOval(x + size * 0.6, y + size * 0.5, size * 0.1, size * 0.1);
    }

    // Draw shopping icon (cart)
    public void drawShoppingIcon(double x, double y, double size) {
        // Cart body
        gc.setFill(Color.FORESTGREEN);
        gc.fillRoundRect(x, y + size * 0.4, size * 0.8, size * 0.4, 5, 5);

        // Cart wheels
        gc.setFill(Color.BLACK);
        gc.fillOval(x + size * 0.15, y + size * 0.8, size * 0.15, size * 0.15);
        gc.fillOval(x + size * 0.6, y + size * 0.8, size * 0.15, size * 0.15);

        // Cart handle
        gc.setStroke(Color.FORESTGREEN);
        gc.setLineWidth(2);
        gc.strokeLine(x + size * 0.8, y + size * 0.4, x + size * 0.8, y + size * 0.1);
        gc.strokeLine(x + size * 0.8, y + size * 0.1, x + size * 0.5, y + size * 0.1);
    }

    // Draw regular checkout icon (cashier)
    public void drawRegularCheckoutIcon( double x, double y, double size) {
        // Register
        gc.setFill(Color.DARKGOLDENROD);
        gc.fillRect(x, y + size * 0.4, size * 0.8, size * 0.4);

        // Screen
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(x + size * 0.1, y + size * 0.1, size * 0.4, size * 0.3);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x + size * 0.1, y + size * 0.1, size * 0.4, size * 0.3);

        // Cashier
        gc.setFill(Color.BLACK);
        gc.fillOval(x + size * 0.6, y + size * 0.15, size * 0.2, size * 0.2);
    }

    // Draw express checkout icon (fast cashier)
    public void drawExpressCheckoutIcon(double x, double y, double size) {
        // Register
        gc.setFill(Color.DARKGOLDENROD);
        gc.fillRect(x, y + size * 0.4, size * 0.8, size * 0.4);

        // Screen
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(x + size * 0.1, y + size * 0.1, size * 0.4, size * 0.3);

        // Fast symbol
        gc.setFill(Color.RED);
        double[] xPoints = {x + size * 0.6, x + size * 0.8, x + size * 0.65, x + size * 0.8, x + size * 0.6};
        double[] yPoints = {y + size * 0.1, y + size * 0.2, y + size * 0.2, y + size * 0.3, y + size * 0.3};
        gc.fillPolygon(xPoints, yPoints, 5);
    }

    // Draw self checkout icon (touchscreen)
    public void drawSelfCheckoutIcon(double x, double y, double size) {
        // Terminal base
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x + size * 0.3, y + size * 0.7, size * 0.4, size * 0.2);

        // Screen
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(x + size * 0.2, y + size * 0.2, size * 0.6, size * 0.5);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x + size * 0.2, y + size * 0.2, size * 0.6, size * 0.5);

        // Touchscreen buttons
        gc.setFill(Color.WHITE);
        gc.fillRect(x + size * 0.3, y + size * 0.3, size * 0.1, size * 0.1);
        gc.fillRect(x + size * 0.5, y + size * 0.3, size * 0.1, size * 0.1);
        gc.fillRect(x + size * 0.3, y + size * 0.5, size * 0.1, size * 0.1);
        gc.fillRect(x + size * 0.5, y + size * 0.5, size * 0.1, size * 0.1);
    }


    // Draw small person icons to represent people in queue
    void drawQueuePersonsIndicator(double x, double y, int queueSize, ServicePointType type) {
        // Limit the display to avoid crowding
        int maxIconsToShow = Math.min(queueSize, 5);

        // Different colors for different checkout types
        Color iconColor;
        switch (type) {
            case EXPRESS_CHECKOUT:
                iconColor = Color.DARKBLUE;
                break;
            case SELF_CHECKOUT:
                iconColor = Color.DARKGRAY;
                break;
            default:
                iconColor = Color.BLACK;
        }

        gc.setFill(iconColor);
        for (int i = 0; i < maxIconsToShow; i++) {
            // Draw simplified person icon
            double iconX = x + i * 12;
            // Head
            gc.fillOval(iconX, y, 6, 6);
            // Body
            gc.fillRect(iconX + 2, y + 6, 2, 7);
        }

        // Show "+" if there are more people than icons shown
        if (queueSize > maxIconsToShow) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            gc.fillText("+" + (queueSize - maxIconsToShow), x + maxIconsToShow * 12 + 2, y + 10);
        }
    }

    // Draw max items indicator for express checkout
    public void drawMaxItemsIndicator(double x, double y) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.fillText("MAX 10", x + 80, y);
    }

//CUSTOMER----------------------------------------------------------
    void drawCustomer(CustomerVisual customer) {
        // Constants for better readability and easier adjustments
        final int IMAGE_SIZE = 40;
        final int ID_BADGE_SIZE = 20;
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
}
