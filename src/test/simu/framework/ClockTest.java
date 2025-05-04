package simu.framework;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ClockTest {
    @BeforeEach
    void setUp() {
        // Reset clock instance for each test
        Clock.getInstance().setTime(0);
    }

    @Test
    void testGetInstance() {
        Clock instance1 = Clock.getInstance();
        Clock instance2 = Clock.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "Multiple calls to getInstance should return the same object");
    }

    @Test
    void testInitialTime() {
        Clock clock = Clock.getInstance();
        assertEquals(0, clock.getTime(), "Initial time should be 0");
    }

    @Test
    void testSetAndGetTime() {
        Clock clock = Clock.getInstance();

        double newTime = 22.5;
        clock.setTime(newTime);
        assertEquals(newTime, clock.getTime(), "Time should be updated to the set value");

        double anotherTime = 6.9;
        clock.setTime(anotherTime);
        assertEquals(anotherTime, clock.getTime(), "Time should be updated to the new set value");
    }
}