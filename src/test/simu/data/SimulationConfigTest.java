package simu.data;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SimulationConfigTest {
    private SimulationConfig config;

    @BeforeEach
    void setUp() {
        config = new SimulationConfig();
    }

    @Test
    void testDefaultValues() {
        assertEquals("Negexp", config.getArrivalDistribution(), "Default arrival distribution should be 'Negexp'");
        assertEquals(5.0, config.getArrivalParam(), "Default arrival parameter should be 5.0");

        assertEquals(20.0, config.getExpressCustomerPercentage(), "Default express customer percentage should be 20.0");

        assertEquals(10, config.getMinRegularItems(), "Default min regular items should be 10");
        assertEquals(30, config.getMaxRegularItems(), "Default max regular items should be 30");

        assertEquals(1, config.getMinExpressItems(), "Default min express items should be 1");
        assertEquals(10, config.getMaxExpressItems(), "Default max express items should be 10");

        assertEquals("Normal", config.getServiceDistribution(), "Default service distribution should be 'Normal'");
        assertEquals(8.0, config.getServiceParam(), "Default service parameter should be 8.0");

        assertEquals(1.0, config.getShoppingMultiplier(), "Default shopping multiplier should be 1.0");
        assertEquals(1.0, config.getRegularMultiplier(), "Default regular checkout multiplier should be 1.0");
        assertEquals(0.7, config.getExpressMultiplier(), "Default express checkout multiplier should be 0.7");
        assertEquals(1.2, config.getSelfCheckoutMultiplier(), "Default self-checkout multiplier should be 1.2");
    }

    @Test
    void testSetAndGetArrivalParameters() {
        String newDistribution = "Normal";
        double newParam = 10.0;

        config.setArrivalDistribution(newDistribution);
        config.setArrivalParam(newParam);

        assertEquals(newDistribution, config.getArrivalDistribution(), "Arrival distribution should now be 'Normal'");
        assertEquals(newParam, config.getArrivalParam(), "Arrival parameter should now be 10.0");
    }

    @Test
    void testSetAndGetCustomerParameters() {
        double newPercentage = 30.0;
        int newMinRegular = 15;
        int newMaxRegular = 40;
        int newMinExpress = 2;
        int newMaxExpress = 15;

        config.setExpressCustomerPercentage(newPercentage);
        config.setMinRegularItems(newMinRegular);
        config.setMaxRegularItems(newMaxRegular);
        config.setMinExpressItems(newMinExpress);
        config.setMaxExpressItems(newMaxExpress);

        assertEquals(newPercentage, config.getExpressCustomerPercentage(), "Express customer percentage should now be 30%");
        assertEquals(newMinRegular, config.getMinRegularItems(), "Min regular items should now be 15");
        assertEquals(newMaxRegular, config.getMaxRegularItems(), "Max regular items should now be 40");
        assertEquals(newMinExpress, config.getMinExpressItems(), "Min express items should now be 2");
        assertEquals(newMaxExpress, config.getMaxExpressItems(), "Max express items should now be 15");
    }

    @Test
    void testSetAndGetServiceParameters() {
        String newDistribution = "Uniform";
        double newParam = 15.0;
        double newShoppingMult = 1.5;
        double newRegularMult = 1.2;
        double newExpressMult = 0.6;
        double newSelfCheckoutMult = 1.3;

        config.setServiceDistribution(newDistribution);
        config.setServiceParam(newParam);
        config.setShoppingMultiplier(newShoppingMult);
        config.setRegularMultiplier(newRegularMult);
        config.setExpressMultiplier(newExpressMult);
        config.setSelfCheckoutMultiplier(newSelfCheckoutMult);

        assertEquals(newDistribution, config.getServiceDistribution(), "Service distribution should now be 'Uniform'");
        assertEquals(newParam, config.getServiceParam(), "Service parameter should now be 15");
        assertEquals(newShoppingMult, config.getShoppingMultiplier(), "Shopping multiplier should now be 1.5");
        assertEquals(newRegularMult, config.getRegularMultiplier(), "Regular multiplier should now be 1.2");
        assertEquals(newExpressMult, config.getExpressMultiplier(), "Express multiplier should now be 0.6");
        assertEquals(newSelfCheckoutMult, config.getSelfCheckoutMultiplier(), "Self-checkout multiplier should now 1.3");
    }
}