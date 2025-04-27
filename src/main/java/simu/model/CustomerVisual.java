package simu.model;

import javafx.scene.paint.Color;

public class CustomerVisual {
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
