package simu.framework;

/**
 * Represents an event in the simulation.
 * Events are scheduled to occur at specific times and have a type that determines
 * the action to be taken when the event is processed.
 * Implements Comparable to allow ordering in priority queues based on event time.
 */
public class Event implements Comparable<Event> {
	private IEventType type;
	private double time;

	/**
	 * Constructs a new event with the specified type and time.
	 *
	 * @param type The type of the event
	 * @param time The simulation time at which the event is scheduled to occur
	 */
	public Event(IEventType type, double time) {
		this.type = type;
		this.time = time;
	}

	/**
	 * Sets the type of the event.
	 *
	 * @param type The new event type
	 */
	public void setType(IEventType type) {
		this.type = type;
	}

	/**
	 * Gets the type of the event.
	 *
	 * @return The event type
	 */
	public IEventType getType() {
		return type;
	}

	/**
	 * Sets the time at which the event is scheduled to occur.
	 *
	 * @param time The new scheduled time
	 */
	public void setTime(double time) {
		this.time = time;
	}

	/**
	 * Gets the time at which the event is scheduled to occur.
	 *
	 * @return The scheduled event time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * Compares this event with another event based on their scheduled times.
	 * Required for ordering events in the priority queue.
	 *
	 * @param arg The event to compare with
	 * @return -1 if this event is scheduled before the other event,
	 *          1 if this event is scheduled after the other event,
	 *          0 if both events are scheduled at the same time
	 */
	@Override
	public int compareTo(Event arg) {
		if (this.time < arg.time) return -1;
		else if (this.time > arg.time) return 1;
		return 0;
	}
}