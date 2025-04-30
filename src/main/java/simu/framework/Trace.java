package simu.framework;

/**
 * Utility class for logging simulation information.
 * Provides different levels of logging and allows filtering based on the trace level.
 */
public class Trace {
	/**
	 * Enum representing different logging levels.
	 * INFO: Informational messages
	 * WAR: Warning messages
	 * ERR: Error messages
	 */
	public enum Level { INFO, WAR, ERR }
	private static Level traceLevel;

	/**
	 * Sets the minimum trace level for logging.
	 * Only messages with a level equal to or higher than this will be logged.
	 *
	 * @param lvl The minimum trace level
	 */
	public static void setTraceLevel(Level lvl){
		traceLevel = lvl;
	}

	/**
	 * Outputs a message if its level is equal to or higher than the current trace level.
	 *
	 * @param lvl The level of the message
	 * @param txt The text to output
	 */
	public static void out(Level lvl, String txt){
		if (lvl.ordinal() >= traceLevel.ordinal()){
			System.out.println(txt);
		}
	}
}