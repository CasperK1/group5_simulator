package simu.framework;

/**
 * Defines the core simulation engine interface.
 * Provides control methods for simulation timing, execution state, and lifecycle management.
 */
public interface IEngine {
    /**
     * Sets the total duration for the simulation run.
     *
     * @param time the simulation duration, in seconds or appropriate units
     */
    void setSimulationTime(double time);

    /**
     * Sets the delay between simulation steps, primarily for visualization pacing.
     *
     * @param time the delay in milliseconds
     */
    void setDelay(long time);

    /**
     * Returns the current delay between simulation steps.
     *
     * @return delay in milliseconds
     */
    long getDelay();

    /**
     * Pauses or resumes the simulation based on the provided flag.
     *
     * @param paused true to pause the simulation, false to resume
     */
    void setPaused(boolean paused);

    /**
     * Resets the simulation engine to its initial state.
     * Should clear internal state and prepare for a new simulation run.
     */
    void reset();
}