package simu.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages saving and loading simulation configurations using Java Properties files.
 * Provides functionality to save current simulation settings and load previously saved configurations.
 */
public class ConfigManager {
    private static final String CONFIG_DIR = "configs";
    private static final String FILE_EXTENSION = ".properties";

    /**
     * Saves the current simulation configuration to a properties file.
     *
     * @param config The simulation configuration to save
     * @param configName The name to give to the saved configuration
     * @throws IOException If an error occurs while writing the file
     */
    public static void saveConfiguration(SimulationConfig config, String configName) throws IOException {
        // Create directory if it doesn't exist
        Files.createDirectories(Paths.get(CONFIG_DIR));

        // Create properties object and populate with configuration values
        Properties props = new Properties();

        // Arrival settings
        props.setProperty("arrivalDistribution", config.getArrivalDistribution());
        props.setProperty("arrivalParam", String.valueOf(config.getArrivalParam()));

        // Customer parameters
        props.setProperty("expressCustomerPercentage", String.valueOf(config.getExpressCustomerPercentage()));
        props.setProperty("minRegularItems", String.valueOf(config.getMinRegularItems()));
        props.setProperty("maxRegularItems", String.valueOf(config.getMaxRegularItems()));
        props.setProperty("minExpressItems", String.valueOf(config.getMinExpressItems()));
        props.setProperty("maxExpressItems", String.valueOf(config.getMaxExpressItems()));

        // Service point parameters
        props.setProperty("serviceDistribution", config.getServiceDistribution());
        props.setProperty("serviceParam", String.valueOf(config.getServiceParam()));
        props.setProperty("shoppingMultiplier", String.valueOf(config.getShoppingMultiplier()));
        props.setProperty("regularMultiplier", String.valueOf(config.getRegularMultiplier()));
        props.setProperty("expressMultiplier", String.valueOf(config.getExpressMultiplier()));
        props.setProperty("selfCheckoutMultiplier", String.valueOf(config.getSelfCheckoutMultiplier()));

        // Default values
        props.setProperty("defaultDelay", String.valueOf(config.getDefaultDelay()));

        // Save properties to file
        String filePath = CONFIG_DIR + File.separator + configName + FILE_EXTENSION;
        try (OutputStream os = new FileOutputStream(filePath)) {
            props.store(os, "Simulation Configuration for " + configName);
        }
    }

    /**
     * Loads a saved simulation configuration from a properties file.
     *
     * @param configName The name of the configuration to load
     * @return The loaded SimulationConfig object
     * @throws IOException If the configuration file cannot be read
     * @throws FileNotFoundException If the specified configuration doesn't exist
     */
    public static SimulationConfig loadConfiguration(String configName) throws IOException {
        String filePath = CONFIG_DIR + File.separator + configName + FILE_EXTENSION;
        File configFile = new File(filePath);

        if (!configFile.exists()) {
            throw new FileNotFoundException("Configuration file not found: " + filePath);
        }

        Properties props = new Properties();
        try (InputStream is = new FileInputStream(configFile)) {
            props.load(is);
        }

        SimulationConfig config = new SimulationConfig();

        // Arrival settings
        config.setArrivalDistribution(props.getProperty("arrivalDistribution", config.getArrivalDistribution()));
        config.setArrivalParam(Double.parseDouble(props.getProperty("arrivalParam", String.valueOf(config.getArrivalParam()))));

        // Customer parameters
        config.setExpressCustomerPercentage(Double.parseDouble(props.getProperty("expressCustomerPercentage",
                String.valueOf(config.getExpressCustomerPercentage()))));
        config.setMinRegularItems(Integer.parseInt(props.getProperty("minRegularItems",
                String.valueOf(config.getMinRegularItems()))));
        config.setMaxRegularItems(Integer.parseInt(props.getProperty("maxRegularItems",
                String.valueOf(config.getMaxRegularItems()))));
        config.setMinExpressItems(Integer.parseInt(props.getProperty("minExpressItems",
                String.valueOf(config.getMinExpressItems()))));
        config.setMaxExpressItems(Integer.parseInt(props.getProperty("maxExpressItems",
                String.valueOf(config.getMaxExpressItems()))));

        // Service point parameters
        config.setServiceDistribution(props.getProperty("serviceDistribution", config.getServiceDistribution()));
        config.setServiceParam(Double.parseDouble(props.getProperty("serviceParam",
                String.valueOf(config.getServiceParam()))));
        config.setShoppingMultiplier(Double.parseDouble(props.getProperty("shoppingMultiplier",
                String.valueOf(config.getShoppingMultiplier()))));
        config.setRegularMultiplier(Double.parseDouble(props.getProperty("regularMultiplier",
                String.valueOf(config.getRegularMultiplier()))));
        config.setExpressMultiplier(Double.parseDouble(props.getProperty("expressMultiplier",
                String.valueOf(config.getExpressMultiplier()))));
        config.setSelfCheckoutMultiplier(Double.parseDouble(props.getProperty("selfCheckoutMultiplier",
                String.valueOf(config.getSelfCheckoutMultiplier()))));

        // Default values
        config.setDefaultDelay(Long.parseLong(props.getProperty("defaultDelay",
                String.valueOf(config.getDefaultDelay()))));

        return config;
    }

    /**
     * Gets a list of all saved configuration names.
     *
     * @return A list of configuration names (without file extensions)
     * @throws IOException If the configurations directory cannot be read
     */
    public static List<String> getSavedConfigurationNames() throws IOException {
        // Create directory if it doesn't exist
        Path dirPath = Paths.get(CONFIG_DIR);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            return Collections.emptyList();
        }

        // Get all .properties files in the directory
        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.endsWith(FILE_EXTENSION))
                    .map(name -> name.substring(0, name.length() - FILE_EXTENSION.length()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Deletes a saved configuration file.
     *
     * @param configName The name of the configuration to delete
     * @return true if the configuration was deleted successfully, false otherwise
     */
    public static boolean deleteConfiguration(String configName) {
        String filePath = CONFIG_DIR + File.separator + configName + FILE_EXTENSION;
        File configFile = new File(filePath);
        return configFile.delete();
    }
}