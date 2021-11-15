package logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;

public class Logger {
	
	private static final String LOG_EXTENSION = ".log.dtd";
	private static boolean enabled = false;
	
	private String prefix;
	private java.util.logging.Logger logger;
	
	public Logger(String className, String prefix) {
		this.logger = java.util.logging.Logger.getLogger(className);
		this.prefix = "[" + prefix + "] ";
	}

	public void info(String msg) {
		if (Logger.enabled) {
			logger.info(this.prefix + msg + "\n");
		}
	}
	
	public void warning(String msg) {
		if (Logger.enabled) {
			logger.warning(this.prefix + msg + "\n");
		}
	}
	
	public void severe(String msg) {
		if (Logger.enabled) {
			logger.severe(this.prefix + msg + "\n");
		}
	}
	
	public void fine(String msg) {
		if (Logger.enabled) {
			logger.fine(this.prefix + msg + "\n");
		}
	}
	
	public static boolean setEnabled(boolean val) {
		Logger.enabled = val;
		return Logger.enabled;
	}
	
	public void setOutputFile(String fileName) throws SecurityException, IOException {
		File logFile = new File("logs/" + fileName + LOG_EXTENSION);
		logFile.getParentFile().mkdirs();
		logger.addHandler(new FileHandler(logFile.getCanonicalPath(), 8096, 1, true));
		logger.setUseParentHandlers(true);
	}
	
	public java.util.logging.Logger getRawLogger() {
		return this.logger;
	}

}
