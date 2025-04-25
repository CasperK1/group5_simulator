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

		arrivalProcess = new ArrivalProcess(new Negexp(5), eventList, EventType.ARR1);

		// Entrance - quick processing
		servicePoints[0] = new ServicePoint(new Negexp(1.0), eventList, EventType.DEP1);

		// Shopping area - TODO: time depends on items
		servicePoints[1] = new ServicePoint(new Normal(15, 5), eventList, EventType.DEP2);

		// Regular checkout
		servicePoints[2] = new ServicePoint(new Normal(8, 3), eventList, EventType.DEP3);

		// Express checkout
		servicePoints[3] = new ServicePoint(new Normal(4, 1), eventList, EventType.DEP4);

		// Self-checkout
		servicePoints[4] = new ServicePoint(new Normal(10, 4), eventList, EventType.DEP5);

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
				customer = new Customer();

				// Add to first service point (entrance)
				servicePoints[0].addQueue(customer);
				controller.customerCreated(customer);
				arrivalProcess.generateNext();
				break;

			case DEP1: // Customer moves from entrance to shopping area
				customer = servicePoints[0].removeQueue();

				// Update location and notify controller
				controller.customerMoved(customer.getId(), ServicePointType.ENTRANCE, ServicePointType.SHOPPING);

				// Start shopping process
				customer.startShopping();
				servicePoints[1].addQueue(customer);
				servicePoints[1].beginService(); // To queue customers into checkout service points. This works, no idea how❓❓
				break;

			case DEP2: // Customer finishes shopping and moves to checkout

				customer = servicePoints[1].removeQueue();
				if (customer == null) { // Null check for beginService() above, otherwise crashes. Still no idea how this works ❓🤓
					System.out.println("Warning: No customer found in shopping area queue");
					break;
				}
				customer.endShopping();


				// Determine which checkout to use based on customer type/items
				ServicePointType checkoutType;
				if (customer.getType() == CustomerType.EXPRESS || customer.getItems() <= 10) {
					checkoutType = ServicePointType.EXPRESS_CHECKOUT;
					customer.setCurrentLocation(checkoutType);
					controller.customerMoved(customer.getId(), ServicePointType.SHOPPING, checkoutType);
					servicePoints[3].addQueue(customer);
				} else {
					// Regular checkout or self-checkout (random choice)
					if (Math.random() > 0.7) { // 30% chance for self-checkout
						checkoutType = ServicePointType.SELF_CHECKOUT;
						customer.setCurrentLocation(checkoutType);
						controller.customerMoved(customer.getId(), ServicePointType.SHOPPING, checkoutType);
						servicePoints[4].addQueue(customer);
					} else {
						checkoutType = ServicePointType.REGULAR_CHECKOUT;
						customer.setCurrentLocation(checkoutType);
						controller.customerMoved(customer.getId(), ServicePointType.SHOPPING, checkoutType);
						servicePoints[2].addQueue(customer);
					}
				}

				customer.startCheckout();

				break;

			case DEP3:
				// Customer leaves regular checkout
				customer = servicePoints[2].removeQueue();
				customer.setRemovalTime(Clock.getInstance().getTime());
				controller.customerCompleted(customer.getId(), ServicePointType.REGULAR_CHECKOUT);
				customer.reportResults();
				break;

			case DEP4:
				// Customer leaves express checkout
				customer = servicePoints[3].removeQueue();
				customer.setRemovalTime(Clock.getInstance().getTime());
				controller.customerCompleted(customer.getId(), ServicePointType.EXPRESS_CHECKOUT);
				customer.reportResults();
				break;

			case DEP5:
				// Customer leaves self-checkout
				customer = servicePoints[4].removeQueue();
				customer.setRemovalTime(Clock.getInstance().getTime());
				controller.customerCompleted(customer.getId(), ServicePointType.SELF_CHECKOUT);
				customer.reportResults();
				break;
		}
	}

	@Override
	protected void results() {
		controller.showEndTime(Clock.getInstance().getTime());
	}
}