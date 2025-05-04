package simu.framework;

import org.junit.jupiter.api.*;
import simu.model.EventType;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    @Test
    void testConstructorAndGetters() {
        IEventType type = EventType.ARR1;
        double time = 15.5;

        Event event = new Event(type, time);

        assertEquals(type, event.getType(), "Event type should match the constructor parameter");
        assertEquals(time, event.getTime(), "Event time should match the constructor parameter");
    }

    @Test
    void testSetters() {
        IEventType initialType = EventType.ARR1;
        double initialTime = 15.5;

        Event event = new Event(initialType, initialTime);

        IEventType newType = EventType.DEP1;
        double newTime = 22.25;

        event.setType(newType);
        event.setTime(newTime);

        assertEquals(newType, event.getType(), "Event type should be updated after setter call");
        assertEquals(newTime, event.getTime(), "Event time should be updated after setter call");
    }

    @Test
    void testCompareTo() {
        Event event1 = new Event(EventType.ARR1, 10.0);
        Event event2 = new Event(EventType.DEP1, 20.0);
        Event event3 = new Event(EventType.DEP2, 10.0);

        assertTrue(event1.compareTo(event2) < 0, "Event 1 with earlier time should be less than Event 2 with later time");
        assertTrue(event2.compareTo(event1) > 0, "Event 2 with later time should be greater than Event 2 with earlier time");
        assertEquals(0, event1.compareTo(event3), "Events 1 and 3 with the same time should be equal in comparison");
    }
}