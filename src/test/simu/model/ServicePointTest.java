package simu.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import eduni.distributions.ContinuousGenerator;
import simu.data.SimulationConfig;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

public class ServicePointTest {
    private ServicePoint servicePoint;
    private ContinuousGenerator generator;
    private EventList eventList;
    private SimulationConfig config;
    private Customer customer;

    @BeforeEach
    void setUp() {
        Clock.getInstance().setTime(0);
        generator = mock(ContinuousGenerator.class);
        eventList = mock(EventList.class);
        servicePoint = new ServicePoint(generator, eventList, EventType.DEP1);
        config = new SimulationConfig();
        customer = new Customer(config);
    }

    @Test
    void testInitialState() {
        assertFalse(servicePoint.isReserved(), "New service point shouldn't be reserved");
        assertFalse(servicePoint.isOnQueue(), "New service point should have an empty queue");
    }

    @Test
    void testAddQueue() {
        servicePoint.addQueue(customer);
        assertTrue(servicePoint.isOnQueue(), "Service point should have a customer in queue");
        assertFalse(servicePoint.isReserved(), "Service point still shouldn't be reserved");
    }

    @Test
    void testBeginService() {
        when(generator.sample()).thenReturn(10.0);
        servicePoint.addQueue(customer);
        servicePoint.beginService();

        assertTrue(servicePoint.isReserved(), "Service point should be reserved after beginning service");
        verify(eventList).add(any(Event.class));
    }

    @Test
    void testBeginServiceShoppingArea() {
        ServicePoint shoppingArea = new ServicePoint(generator, eventList, EventType.ARR1);
        customer.setItems(15);
        shoppingArea.addQueue(customer);
        shoppingArea.beginService();

        assertTrue(shoppingArea.isReserved(), "Shopping service point should be reserved after beginning service");
        verify(eventList).add(any(Event.class));
    }

    @Test
    void testRemoveQueue() {
        when(generator.sample()).thenReturn(10.0);
        servicePoint.addQueue(customer);
        servicePoint.beginService();

        assertTrue(servicePoint.isReserved(), "Service point should be reserved after beginning service");

        Customer removedCustomer = servicePoint.removeQueue();

        assertSame(customer, removedCustomer, "Removed customer should be the same as added customer");
        assertFalse(servicePoint.isReserved(), "Service point shouldn't be reserved after removing customer");
        assertFalse(servicePoint.isOnQueue(), "Queue should be empty after removing the only customer");
    }

    @Test
    void testBeginServiceWithEmptyQueue() {
        servicePoint.beginService();
        assertFalse(servicePoint.isReserved(), "Service point shouldn't be reserved when queue is empty");
        verify(eventList, never()).add(any(Event.class));
    }
}