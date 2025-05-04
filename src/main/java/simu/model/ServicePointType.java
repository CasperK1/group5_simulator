package simu.model;

/**
 * Enumeration of service point types in the store simulation.
 * Defines the different locations where customers can be served in the system.
 */
public enum ServicePointType {
    /**
     * Store entrance where customers enter the system.
     */
    ENTRANCE,

    /**
     * Shopping area where customers browse and collect items.
     */
    SHOPPING,

    /**
     * Regular checkout lanes for customers with standard shopping baskets.
     */
    REGULAR_CHECKOUT,

    /**
     * Express checkout lanes for customers with few items.
     */
    EXPRESS_CHECKOUT,

    /**
     * Self-checkout area where customers scan their own items.
     */
    SELF_CHECKOUT
}