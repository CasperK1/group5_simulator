package simu.framework;

import java.util.PriorityQueue;

/**
 * Manages the list of scheduled events in the simulation.
 * Uses a PriorityQueue to automatically order events by their scheduled time.
 */
public class EventList {
	private PriorityQueue<Event> lista = new PriorityQueue<Event>();

	/**
	 * Constructs a new empty event list.
	 */
	public EventList() {
	}

	/**
	 * Removes and returns the next event from the list.
	 * The next event is the one with the earliest scheduled time.
	 *
	 * @return The next event to be processed
	 */
	public Event remove(){
		return lista.remove();
	}

	/**
	 * Adds a new event to the list.
	 * The event will be automatically ordered based on its scheduled time.
	 *
	 * @param t The event to add
	 */
	public void add(Event t){
		lista.add(t);
	}

	/**
	 * Gets the scheduled time of the next event without removing it from the list.
	 *
	 * @return The scheduled time of the next event
	 */
	public double getNextTime(){
		return lista.peek().getTime();
	}
}