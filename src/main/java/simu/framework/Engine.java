package simu.framework;

import controller.IControllerMtoV;
import simu.model.ServicePoint;

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
	 * Checks if the simulation is currently paused.
	 *
	 * @return True if the simulation is paused, false otherwise
	 */
	@Override
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Runs the simulation.
	 * Initializes the simulation, processes events, and produces results.
	 */
	@Override
	public void run() {
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

		results();
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
	 * To be implemented by concrete subclasses.
	 */
	protected abstract void initialization();

	/**
	 * Processes a single event.
	 * To be implemented by concrete subclasses.
	 *
	 * @param t The event to process
	 */
	protected abstract void runEvent(Event t);

	/**
	 * Produces and reports the simulation results.
	 * To be implemented by concrete subclasses.
	 */
	protected abstract void results();
}