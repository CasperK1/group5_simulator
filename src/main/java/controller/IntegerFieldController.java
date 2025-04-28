package controller;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Utility class for setting up and validating JavaFX text fields that accept integer values.
 * Provides validation, error feedback, and automatic correction for integer input.
 */
public class IntegerFieldController {

    /**
     * Configures a TextField to accept and validate integer values within a specified range.
     *
     * @param field The TextField to configure
     * @param min The minimum allowed value (inclusive)
     * @param max The maximum allowed value (inclusive)
     * @param setter Consumer function to call when a valid value is entered
     */
    public static void setupField(TextField field, int min, int max,
                                  java.util.function.Consumer<Integer> setter) {
        // Create a tooltip for validation errors
        Tooltip errorTooltip = new Tooltip("Value must be between " + min + " and " + max);
        errorTooltip.setStyle("-fx-background-color: salmon; -fx-text-fill: white;");
        // Tooltip for non-integer input
        Tooltip formatErrorTooltip = new Tooltip("Please enter a valid integer.");
        formatErrorTooltip.setStyle("-fx-background-color: salmon; -fx-text-fill: white;");


        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                field.setTooltip(formatErrorTooltip);
                return;
            }

            try {
                int value = Integer.parseInt(newVal.trim());

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

        // Optional: Add a focus lost listener to revert if still invalid when focus is lost
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // Lost focus
                // Re-validate the current text
                String currentText = field.getText();
                if (currentText == null || currentText.trim().isEmpty()) {
                    // Handle empty case if needed on focus lost, maybe revert to old valid value
                    // or a default. Example: revert to min
                    field.setText(String.valueOf(min));
                    return;
                }
                try {
                    int value = Integer.parseInt(currentText.trim());
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

    /**
     * Configures two text fields as a min/max pair, ensuring that the min value never exceeds the max value.
     * Both fields are validated individually, and also constrained relative to each other.
     *
     * @param minField The minimum value TextField
     * @param maxField The maximum value TextField
     * @param absoluteMin The lowest allowed value for either field
     * @param absoluteMax The highest allowed value for either field
     * @param minSetter Consumer function to call when a valid min value is entered
     * @param maxSetter Consumer function to call when a valid max value is entered
     */
    public static void setupPairedFields(TextField minField, TextField maxField,
                                         int absoluteMin, int absoluteMax,
                                         java.util.function.Consumer<Integer> minSetter,
                                         java.util.function.Consumer<Integer> maxSetter) {

        // Internal state to track the last valid values, needed for paired validation logic
        final int[] lastValidMin = {absoluteMin};
        final int[] lastValidMax = {absoluteMax};

        setupField(minField, absoluteMin, absoluteMax, value -> {
            minSetter.accept(value);
            lastValidMin[0] = value;
            if (value > lastValidMax[0]) {
                maxField.setText(String.valueOf(value));
            }
        });
        setupField(maxField, absoluteMin, absoluteMax, value -> {
            maxSetter.accept(value);
            lastValidMax[0] = value; // Store the last valid max value
            // Check pair validity after max changes *and* is valid
            if (value < lastValidMin[0]) {
                minField.setText(String.valueOf(value));
            }
        });

        // Initialize fields with valid values
        try {
            int initialMin = Integer.parseInt(minField.getText());
            if (initialMin < absoluteMin || initialMin > absoluteMax) throw new NumberFormatException();
            lastValidMin[0] = initialMin;
        } catch (Exception e) {
            minField.setText(String.valueOf(absoluteMin)); // Set to default min
        }

        try {
            int initialMax = Integer.parseInt(maxField.getText());
            if (initialMax < absoluteMin || initialMax > absoluteMax || initialMax < lastValidMin[0]) throw new NumberFormatException();
            lastValidMax[0] = initialMax;
        } catch (Exception e) {
            int minVal = lastValidMin[0];
            maxField.setText(String.valueOf(Math.max(minVal, absoluteMin)));
        }

        int currentMin = lastValidMin[0];
        int currentMax = lastValidMax[0];
        if(currentMin > currentMax) {
            maxField.setText(String.valueOf(currentMin));
        }
    }
}