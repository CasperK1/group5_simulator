package simu.model;

import controller.IControllerMtoV;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.ArrivalProcess;
import simu.framework.Event;

public class MyEngine extends Engine {
	private ArrivalProcess arrivalProcess;

	public MyEngine(IControllerMtoV controller) {
		super(controller);

		// Create service points for the store simulation
		servicePoints = new ServicePoint[5];

		// Entrance - quick processing
		servicePoints[0] = new ServicePoint(new Negexp(1.0), eventList, EventType.DEP1);

		// Shopping area - time depends on items
		servicePoints[1] = new ServicePoint(new Normal(15, 5), eventList, EventType.DEP2);

		// Regular checkout
		servicePoints[2] = new ServicePoint(new Normal(8, 3), eventList, EventType.DEP3);

		// Express checkout
		servicePoints[3] = new ServicePoint(new Normal(4, 1), eventList, EventType.DEP4);

		// Self-checkout
		servicePoints[4] = new ServicePoint(new Normal(10, 4), eventList, EventType.DEP5);

		// For simplicity, continue to use ArrivalProcess for customer generation
		arrivalProcess = new ArrivalProcess(new Negexp(5), eventList, EventType.ARR1);
	}

	@Override
	protected void initialization() {
		arrivalProcess.generateNext(); // Schedule the first arrival
	}

	@Override
	protected void runEvent(Event t) {
		Customer customer;

		switch ((EventType)t.getType()) {
			case ARR1:
				// Create a new customer
				customer = new Customer();

				// Add to first service point (entrance)
				servicePoints[0].addQueue(customer);

				// Generate next arrival
				arrivalProcess.generateNext();

				// Notify controller to visualize the customer
				controller.visualiseCustomer();
				break;

			case DEP1:
				// Customer moves from entrance to shopping area
				customer = servicePoints[0].removeQueue();
				servicePoints[1].addQueue(customer);
				break;

			case DEP2:
				// Customer finishes shopping and moves to a checkout
				// For simplicity, we'll always use regular checkout for now
				customer = servicePoints[1].removeQueue();
				servicePoints[2].addQueue(customer);
				break;

			case DEP3:
				// Customer leaves regular checkout
				customer = servicePoints[2].removeQueue();
				customer.setRemovalTime(Clock.getInstance().getTime());
				customer.reportResults();
				break;

			case DEP4:
				// Customer leaves express checkout
				customer = servicePoints[3].removeQueue();
				customer.setRemovalTime(Clock.getInstance().getTime());
				customer.reportResults();
				break;

			case DEP5:
				// Customer leaves self-checkout
				customer = servicePoints[4].removeQueue();
				customer.setRemovalTime(Clock.getInstance().getTime());
				customer.reportResults();
				break;
		}
	}

	@Override
	protected void results() {
		controller.showEndTime(Clock.getInstance().getTime());
	}
}