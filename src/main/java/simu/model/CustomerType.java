package simu.model;

/**
 * Enumeration of customer types in the store simulation.
 * Defines the possible categories of customers based on their shopping behavior.
 */
public enum CustomerType {
    /**
     * Regular customer with normal amount of items.
     * Typically shops with more items and uses regular checkout lanes.
     */
    REGULAR,

    /**
     * Express customer with few items.
     * Shops with fewer items and is eligible for express checkout lanes.
     */
    EXPRESS
}