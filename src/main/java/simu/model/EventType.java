package simu.model;

import simu.framework.IEventType;

public enum EventType implements IEventType {
	ARR1,   // Arrival at entrance
	DEP1,   // Departure from entrance
	DEP2,   // Departure from shopping area
	DEP3,   // Departure from regular checkout
	DEP4,   // Departure from express checkout
	DEP5;   // Departure from self-checkout
}