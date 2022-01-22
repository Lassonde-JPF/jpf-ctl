package logging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public class Logger {
	
	// Extensions
	private static final String LOG_EXTENSION = ".log.dtd";
	
	// Set of all loggers
	private static List<java.util.logging.Logger> loggers = new ArrayList<java.util.logging.Logger>();	
	
	// This specific logger
	private java.util.logging.Logger logger;
	
	public Logger(String className) {
		// Create a java.util.logging.Logger object
		this.logger = java.util.logging.Logger.getLogger(className);
		
		// Remove parent handlers -> i.e default console handler
		this.logger.setUseParentHandlers(false);
		
		// Replace with better, new console handler (shiny, wow!)
		ConsoleHandler cH = new ConsoleHandler();
		cH.setFormatter(new LogFormatter());
		this.logger.addHandler(cH);
		
		// Record this logger in the big book of loggers 
		Logger.loggers.add(this.logger);
	}

	public void info(String msg) {
		this.logger.info(msg);
	}
	
	public void warning(String msg) {
		this.logger.warning(msg);
	}
	
	public void severe(String msg) {
		this.logger.severe(msg);
	}
	
	public void fine(String msg) {
		this.logger.fine(msg);
	}
	
	public static void setEnabled(boolean val) {
		// Turn off logs for all levels
		Logger.loggers.stream().forEach(logger -> logger.setLevel(val ? Level.ALL : Level.OFF));
	}
	
	// Set Formatter (default xml)
	public void setOutputFile() throws SecurityException, IOException {
		// Build Log Name
		SimpleDateFormat df = new SimpleDateFormat("D_hh_mm");
		String logName = "[jpf-ctl]" + df.format(new Date());
		File logFile = new File("logs/" + logName + LOG_EXTENSION);
		logFile.getParentFile().mkdirs();
		logger.addHandler(new FileHandler(logFile.getCanonicalPath(), 0, 1, true));
	}
	
	public static void clrscr(){
	    //Clears Screen in java
	    try {
	        if (System.getProperty("os.name").contains("Windows"))
	            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	        else
	            Runtime.getRuntime().exec("clear");
	    } catch (IOException | InterruptedException ex) {}
	}

}
