package simu.model;

import simu.data.SimulationConfig;
import simu.framework.Clock;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    private SimulationConfig config;
    private Customer customer;

    @BeforeEach
    void setUp() {
        Clock.getInstance().setTime(0);
        Customer.resetStatistics();
        config = new SimulationConfig();
        customer = new Customer(config);
    }

    @Test
    void testCustomerCreation() {
        assertEquals(0, customer.getArrivalTime(), "Arrival time should be the current clock time");
        assertNotNull(customer.getType(), "Customer type should not be null");
        assertTrue(customer.getItems() > 0, "Customer should have at least one item");

        assertTrue(customer.getId() > 0, "Customer ID should be greater than 0");

        assertEquals(ServicePointType.ENTRANCE, customer.getCurrentLocation(), "Initial location should be the entrance");
        assertNull(customer.getPreviousLocation(), "Previous location should be null for new customer");
    }

    @Test
    void testExpressCustomerItems() {
        config.setExpressCustomerPercentage(100);
        config.setMinExpressItems(1);
        config.setMaxExpressItems(8);

        Customer expressCustomer = new Customer(config);

        assertEquals(CustomerType.EXPRESS, expressCustomer.getType(), "Customer type should be express");
        assertTrue(expressCustomer.getItems() >= 1 && expressCustomer.getItems() <= 8, "Express customer should have items between the min and max amount");
    }

    @Test
    void testRegularCustomerItems() {
        config.setExpressCustomerPercentage(0);
        config.setMinRegularItems(15);
        config.setMaxRegularItems(25);

        Customer regularCustomer = new Customer(config);

        assertEquals(CustomerType.REGULAR, regularCustomer.getType(), "Customer type should be regular");
        assertTrue(regularCustomer.getItems() >= 15 && regularCustomer.getItems() <= 25, "Regular customer should have items between the min and max amount");
    }

    @Test
    void testLocationUpdates() {
        assertNull(customer.getPreviousLocation(), "Previous location should initially be null");
        assertEquals(ServicePointType.ENTRANCE, customer.getCurrentLocation(), "Initial location should be the entrance");

        customer.setCurrentLocation(ServicePointType.SHOPPING);
        assertEquals(ServicePointType.SHOPPING, customer.getCurrentLocation(), "Current location should now be the shopping area");
        assertEquals(ServicePointType.ENTRANCE, customer.getPreviousLocation(), "Previous location should be the entrance");
    }

    @Test
    void testTime() {
        Clock.getInstance().setTime(10);
        customer.startShopping();

        Clock.getInstance().setTime(30);
        customer.endShopping();

        assertEquals(20, customer.getShoppingDuration(), "Shopping duration should be end time - start time");

        Clock.getInstance().setTime(35);
        customer.startCheckout();

        Clock.getInstance().setTime(45);
        customer.setRemovalTime(45);

        assertEquals(10, customer.getCheckoutDuration(), "Checkout duration should be removal time - checkout start time");
        assertEquals(45, customer.getTotalTime(), "Total time should be removal time - arrival time");
    }

    @Test
    void testStaticStatistics() {
        assertEquals(0, Customer.getTotalCompletedCustomers(), "Should start with 0 completed customers");
        assertEquals(0, Customer.getMeanServiceTime(), "Mean service time should start at 0");

        customer.setArrivalTime(10);
        customer.setRemovalTime(30);
        customer.reportResults();

        assertEquals(1, Customer.getTotalCompletedCustomers(), "Should have 1 completed customer");
        assertEquals(20, Customer.getMeanServiceTime(), "Mean service time should be 20");

        Customer customer2 = new Customer(config);
        customer2.setArrivalTime(15);
        customer2.setRemovalTime(45);
        customer2.reportResults();

        assertEquals(2, Customer.getTotalCompletedCustomers(), "Should have 2 completed customers");
        assertEquals(25, Customer.getMeanServiceTime(), "Mean service time should be 25)");
    }
}