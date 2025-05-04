package simu.framework;

/**
 * A singleton class that represents the simulation clock.
 * Keeps track of the current simulation time.
 */
public class Clock {
	private double time;
	private static Clock instance;

	/**
	 * Initializes the clock time to 0.
	 */
	private Clock(){
		time = 0;
	}

	/**
	 * Returns the singleton instance of the Clock.
	 * Creates a new instance if one doesn't already exist.
	 *
	 * @return The singleton Clock instance
	 */
	public static Clock getInstance(){
		if (instance == null){
			instance = new Clock();
		}
		return instance;
	}

	/**
	 * Sets the current simulation time.
	 *
	 * @param time The new simulation time
	 */
	public void setTime(double time){
		this.time = time;
	}

	/**
	 * Gets the current simulation time.
	 *
	 * @return The current simulation time
	 */
	public double getTime(){
		return time;
	}
}