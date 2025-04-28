package simu.model;

import java.util.LinkedList;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

// TODO:
// Service Point functionalities & calculations (+ variables needed) and reporting to be implemented
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

	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type){
		this.eventList = eventList;
		this.generator = generator;
		this.eventTypeScheduled = type;
	}

	public void addQueue(Customer a){   // First customer at the queue is always on the service
		queue.add(a);
	}

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
		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getTime()+serviceTime));
	}
	public boolean isReserved(){
		return reserved;
	}

	public boolean isOnQueue(){
		return queue.size() != 0;
	}


}
