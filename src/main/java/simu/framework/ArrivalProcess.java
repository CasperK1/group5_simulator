package simu.framework;
import eduni.distributions.*;
import simu.model.EventType;

/**
 * Represents the arrival process in the simulation.
 * Responsible for generating new arrival events at the appropriate times.
 */
public class ArrivalProcess {
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType type;

	/**
	 * Constructs a new arrival process with the specified parameters.
	 *
	 * @param g The continuous generator used to generate time intervals between arrivals
	 * @param tl The event list where new arrival events will be added
	 * @param type The type of event to be generated
	 */
	public ArrivalProcess(ContinuousGenerator g, EventList tl, EventType type) {
		this.generator = g;
		this.eventList = tl;
		this.type = type;
	}

	/**
	 * Generates the next arrival event and adds it to the event list.
	 * The arrival time is determined by adding a sample from the generator
	 * to the current simulation time.
	 */
	public void generateNext() {
		Event t = new Event(type, Clock.getInstance().getTime() + generator.sample());
		eventList.add(t);
	}
}