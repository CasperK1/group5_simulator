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
		return queue.poll();
	}

	public void beginService() {
		if (queue.isEmpty()) {
			return;
		}
		reserved = true;
		double serviceTime = generator.sample();
		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getTime()+serviceTime));
	}
	public boolean isReserved(){
		return reserved;
	}

	public boolean isOnQueue(){
		return queue.size() != 0;
	}
}
