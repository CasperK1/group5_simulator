package simu.framework;

import controller.IControllerMtoV;
import simu.model.ServicePoint;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Abstract base class for the simulation engine.
 * Extends Thread to run the simulation in a separate thread.
 * Implements the IEngine interface to provide control over the simulation.
 */
public abstract class Engine extends Thread implements IEngine {
    private double simulationTime = 0;
    private long delay = 0;
    private Clock clock;
    private boolean paused = false;
    private Timer timeLeftTimer;
    private static final int TIME_UPDATE_INTERVAL = 1000; // Update time left every second

    protected EventList eventList;
    protected ServicePoint[] servicePoints;
    protected IControllerMtoV controller;

    /**
     * Constructs a new Engine with the specified controller.
     *
     * @param controller The controller that mediates between the model and view
     */
    public Engine(IControllerMtoV controller) {
        this.controller = controller;
        clock = Clock.getInstance();
        eventList = new EventList();
    }

    /**
     * Sets the total simulation time.
     *
     * @param time The duration of the simulation
     */
    @Override
    public void setSimulationTime(double time) {
        simulationTime = time;
    }

    /**
     * Sets the delay between simulation steps for visualization.
     *
     * @param time The delay in milliseconds
     */
    @Override
    public void setDelay(long time) {
        this.delay = time;
    }

    /**
     * Gets the current delay between simulation steps.
     *
     * @return The current delay in milliseconds
     */
    @Override
    public long getDelay() {
        return delay;
    }

    /**
     * Sets the paused state of the simulation.
     *
     * @param paused True to pause the simulation, false to resume
     */
    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Runs the simulation.
     * Initializes the simulation, processes events, and produces results.
     */
    @Override
    public void run() {
        startTimeLeftCounter();
        initialization();
        while (simulate()){
            while (paused) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            delay();
            clock.setTime(currentTime());
            runBEvents();
            tryCEvents();
        }

        stopTimeLeftCounter();
        controller.updateTimeLeft(0);
        results();
    }

    /**
     * Starts a timer to update the UI with the estimated time left
     */
    private void startTimeLeftCounter() {
        timeLeftTimer = new Timer(true);
        timeLeftTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimeLeft();
            }
        }, 0, TIME_UPDATE_INTERVAL);
    }

    /**
     * Stops the time left counter
     */
    private void stopTimeLeftCounter() {
        if (timeLeftTimer != null) {
            timeLeftTimer.cancel();
            timeLeftTimer.purge();
            timeLeftTimer = null;
        }
    }

    /**
     * Updates the UI with the estimated time left
     * Uses a simple formula based on current time, total time, and delay
     */
    private void updateTimeLeft() {
        if (paused || simulationTime <= 0) {
            return;
        }
        // remaining events * delay per event
        double remainingSimTime = simulationTime - clock.getTime();
        if (remainingSimTime <= 0) {
            controller.updateTimeLeft(0);
            return;
        }

        // Estimate time left in seconds based on delay
        int secondsLeft = (int)((remainingSimTime * delay) / 1000);
        controller.updateTimeLeft(secondsLeft);
    }

    /**
     * Resets the simulation engine to its initial state.
     * Stops the time left counter and resets various components.
     */
    public void reset() {
        // Stop the time left counter and reset the UI
        stopTimeLeftCounter();
        controller.updateTimeLeft(-1);

        // Reset the simulation time
        simulationTime = 0;

        // Reset the event list (clear any remaining events)
        eventList.clear(); // If using PriorityQueue, clear it

        // Reset paused state
        paused = false;

        // Optionally, reset the clock or service points if necessary
        clock.setTime(0);

        // Reinitialize service points if needed
        for (ServicePoint sp : servicePoints) {
            sp.reset(); // Assuming you have a reset method in ServicePoint
        }
    }

    /**
     * Processes all B-phase events scheduled for the current time.
     */
    private void runBEvents() {
        while (eventList.getNextTime() == clock.getTime()){
            runEvent(eventList.remove());
        }
    }

    /**
     * Attempts to start service at all service points that have customers waiting.
     * This represents the C-phase events in the simulation.
     */
    private void tryCEvents() {
        for (ServicePoint p: servicePoints){
            if (!p.isReserved() && p.isOnQueue()){
                p.beginService();
            }
        }
    }

    /**
     * Gets the time of the next scheduled event.
     *
     * @return The time of the next event
     */
    private double currentTime(){
        return eventList.getNextTime();
    }

    /**
     * Checks if the simulation should continue.
     *
     * @return True if the simulation should continue, false otherwise
     */
    private boolean simulate() {
        Trace.out(Trace.Level.INFO, "Time is: " + clock.getTime());
        return clock.getTime() < simulationTime;
    }

    /**
     * Delays the simulation thread based on the current delay setting.
     */
    private void delay() {
        Trace.out(Trace.Level.INFO, "Delay " + delay);
        try {
            sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the simulation.
     */
    protected abstract void initialization();

    /**
     * Processes a single event.
     *
     * @param t The event to process
     */
    protected abstract void runEvent(Event t);

    /**
     * Produces and reports the simulation results.
     */
    protected abstract void results();
}