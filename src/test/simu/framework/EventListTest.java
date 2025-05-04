package simu.framework;

import org.junit.jupiter.api.*;
import simu.model.EventType;
import static org.junit.jupiter.api.Assertions.*;

class EventListTest {
    private EventList eventList;

    @BeforeEach
    void setUp() {
        // Sets up a new event list before each test
        eventList = new EventList();
    }

    @Test
    void testAddAndRemove() {
        Event event1 = new Event(EventType.ARR1, 10.0);
        Event event2 = new Event(EventType.DEP1, 20.0);

        eventList.add(event1);
        eventList.add(event2);

        Event removed = eventList.remove();
        assertEquals(event1, removed, "First event removed should be the one with the earliest time");

        removed = eventList.remove();
        assertEquals(event2, removed, "Second event removed should be the one with the later time");
    }

    @Test
    void testPriorityOrder() {
        Event laterEvent = new Event(EventType.ARR1, 30.0);
        Event earlierEvent = new Event(EventType.DEP1, 10.0);
        Event middleEvent = new Event(EventType.DEP2, 20.0);

        eventList.add(laterEvent);
        eventList.add(earlierEvent);
        eventList.add(middleEvent);

        assertEquals(earlierEvent, eventList.remove(), "First event removed should be the earliest one");
        assertEquals(middleEvent, eventList.remove(), "Second event removed should be the middle one");
        assertEquals(laterEvent, eventList.remove(), "Third event removed should be the latest one");
    }

    @Test
    void testGetNextTime() {
        Event event1 = new Event(EventType.ARR1, 10.0);
        Event event2 = new Event(EventType.DEP1, 20.0);

        eventList.add(event1);
        eventList.add(event2);

        assertEquals(10.0, eventList.getNextTime(), "Next time should the time of the earliest event");

        eventList.remove();

        assertEquals(20.0, eventList.getNextTime(), "Next time should now be the time the next earliest event");
    }
}