package controller;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Utility class for setting up and validating JavaFX text fields that accept double values.
 * Provides validation, error feedback, and automatic correction for decimal input.
 */
public class DoubleFieldController {

    /**
     * Configures a TextField to accept and validate double values within a specified range.
     *
     * @param field The TextField to configure
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @param setter Function to call when a valid value is entered
     */
    public static void setupField(TextField field, double min, double max,
                                  java.util.function.Consumer<Double> setter) {
        // Create tooltips for validation errors
        Tooltip errorTooltip = new Tooltip("Value must be between " + min + " and " + max);
        errorTooltip.setStyle("-fx-background-color: salmon; -fx-text-fill: white;");
        // Tooltip for non-double input
        Tooltip formatErrorTooltip = new Tooltip("Please enter a valid decimal number.");
        formatErrorTooltip.setStyle("-fx-background-color: salmon; -fx-text-fill: white;");

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                field.setTooltip(formatErrorTooltip);
                return;
            }

            try {
                double value = Double.parseDouble(newVal.trim());

                if (value >= min && value <= max) {
                    setter.accept(value);
                    field.setStyle("");
                    field.setTooltip(null);
                } else {
                    field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    field.setTooltip(errorTooltip);
                }
            } catch (NumberFormatException e) {
                // Invalid number format - show error
                field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                field.setTooltip(formatErrorTooltip);
            }
        });

        // Add a focus lost listener to revert if still invalid when focus is lost
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // Lost focus
                // Re-validate the current text
                String currentText = field.getText();
                if (currentText == null || currentText.trim().isEmpty()) {
                    // Handle empty case - revert to min
                    field.setText(String.valueOf(min));
                    return;
                }
                try {
                    double value = Double.parseDouble(currentText.trim());
                    if (value < min || value > max) {
                        if (value < min) field.setText(String.valueOf(min));
                        else field.setText(String.valueOf(max));
                    }
                } catch (NumberFormatException e) {
                    field.setText(String.valueOf(min));
                }
            }
        });
    }
}