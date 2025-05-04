package simu.model;

import java.util.LinkedList;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

/**
 * Represents a service point in the simulation.
 * Handles customer queue management and service scheduling.
 * Tracks statistics about service operations.
 */
public class ServicePoint {
	private LinkedList<Customer> queue = new LinkedList<Customer>();
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType eventTypeScheduled;
	private boolean reserved = false;

	private int customersServed = 0;
	private double totalServiceTime = 0.0;
	private double totalWaitingTime = 0.0;
	private double lastServiceStartTime = 0.0;

	/**
	 * Creates a new service point with the specified parameters.
	 *
	 * @param generator The continuous generator used for service time sampling
	 * @param eventList The event list where departure events will be scheduled
	 * @param type The event type to be generated upon service completion
	 */
	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type){
		this.eventList = eventList;
		this.generator = generator;
		this.eventTypeScheduled = type;
	}

	/**
	 * Adds a customer to the service queue.
	 *
	 * @param a The customer to be added to the queue
	 */
	public void addQueue(Customer a){   // First customer at the queue is always on the service
		queue.add(a);
	}

	/**
	 * Removes and returns the customer at the front of the queue.
	 * Marks the service point as no longer reserved and updates service statistics.
	 *
	 * @return The customer who completed service
	 */
	public Customer removeQueue(){		// Remove serviced customer
		reserved = false;
		Customer servedCustomer = queue.poll();

		if (servedCustomer != null) {
			double serviceDuration = Clock.getInstance().getTime() - lastServiceStartTime;
			totalServiceTime += serviceDuration;
			customersServed++;
		}

		return servedCustomer;
	}

	/**
	 * Begins service for the customer at the front of the queue.
	 * Samples a service time from the generator or calculates it based on customer items,
	 * and schedules a departure event.
	 * Marks the service point as reserved during service.
	 */
	public void beginService() {
		if (queue.isEmpty()) {
			return;
		}
		reserved = true;

		Customer currentCustomer = queue.peek(); // Peek without removing
		double serviceTime;

		if (eventTypeScheduled == EventType.DEP2) { // Shopping service point
			int items = currentCustomer.getItems();
			double baseTime = 10.0;      // seconds to "enter" shopping
			double timePerItem = 2.0;   // seconds per item - LOWERED THIS TO PREVENT LAG
			serviceTime = baseTime + timePerItem * items;
			System.out.println("Shopping time: " + serviceTime + "Custom Id: " + currentCustomer.getId() + "Item: " + currentCustomer.getItems());
		} else {
			serviceTime = generator.sample();
		}
		lastServiceStartTime = Clock.getInstance().getTime();
		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getTime()+serviceTime));
	}

	/**
	 * Checks if the service point is currently servicing a customer.
	 *
	 * @return True if the service point is reserved, false otherwise
	 */
	public boolean isReserved(){
		return reserved;
	}

	/**
	 * Checks if there are any customers in the queue.
	 *
	 * @return True if there are customers in the queue, false otherwise
	 */
	public boolean isOnQueue(){
		return queue.size() != 0;
	}

	/**
	 * Resets the service point to its initial state.
	 * Clears the queue and all tracked statistics.
	 */
	public void reset() {
		queue.clear();
		reserved = false;
		customersServed = 0;
		totalServiceTime = 0.0;
		totalWaitingTime = 0.0;
		lastServiceStartTime = 0.0;
	}
}