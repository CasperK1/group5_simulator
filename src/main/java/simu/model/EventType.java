package simu.model;

import simu.framework.IEventType;

/**
 * Enumeration of event types used in the store simulation.
 * Defines the different events that can occur during the simulation,
 * representing customer arrivals and departures from different service points.
 */
public enum EventType implements IEventType {
	/**
	 * Customer arrival at the entrance of the store.
	 */
	ARR1,

	/**
	 * Customer departure from the entrance to the shopping area.
	 */
	DEP1,

	/**
	 * Customer departure from the shopping area to a checkout.
	 */
	DEP2,

	/**
	 * Customer departure from the regular checkout (complete service).
	 */
	DEP3,

	/**
	 * Customer departure from the express checkout (complete service).
	 */
	DEP4,

	/**
	 * Customer departure from the self-checkout (complete service).
	 */
	DEP5;
}