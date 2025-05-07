package view;

/**
 * Represents the visualisation component responsible for displaying and resetting the simulation UI.
 */
public interface IVisualisation {
    /**
     * Clears all elements from the display, resetting the visual state.
     */
    void clearDisplay();

    /**
     * Resets the display to its initial state.
     * May include clearing elements and resetting any display-specific data.
     */
    void resetDisplay();
}