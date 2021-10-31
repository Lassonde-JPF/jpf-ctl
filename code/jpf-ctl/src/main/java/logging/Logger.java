package logging;

import java.io.IOException;
import java.util.logging.FileHandler;

public class Logger {
	
	private String prefix;
	
	private java.util.logging.Logger logger;
	
	public Logger(String className, String prefix) {
		this.logger = java.util.logging.Logger.getLogger(className);
		this.prefix = "[" + prefix + "] ";
	}

	public void info(String msg) {
		logger.info(this.prefix + msg);
	}
	
	public void warning(String msg) {
		logger.warning(this.prefix + msg);
	}
	
	public void severe(String msg) {
		logger.severe(this.prefix + msg);
	}
	
	public void fine(String msg) {
		logger.fine(this.prefix + msg);
	}
	
	public boolean setOutputFile(String fileName) {
		try {
			logger.addHandler( new FileHandler("logs/" + fileName));
			return true;
		} catch (SecurityException | IOException e) {
			return false;
		}
	}

}
