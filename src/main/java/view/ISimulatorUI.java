package view;

/**
 * Defines the contract for the Simulator User Interface (UI) component.
 * Provides methods for retrieving simulation input, interacting with the visualisation layer,
 * and updating the UI based on simulation progress.
 */
public interface ISimulatorUI {
    /**
     * Returns the simulation time specified by the user.
     *
     * @return the duration for which the simulation should run, in seconds or units defined by the implementation
     */
    double getTime();

    /**
     * Returns the current delay between simulation steps, typically used for visualization pacing.
     *
     * @return the delay in milliseconds
     */
    long getDelay();

    /**
     * Retrieves the visualisation component associated with the UI.
     *
     * @return the IVisualisation implementation used to update the simulation's visual state
     */
    IVisualisation getVisualisation();

    /**
     * Updates the UI to show the time left in the simulation.
     *
     * @param secondsLeft the remaining time in seconds
     */
    void updateTimeLeft(int secondsLeft);
}