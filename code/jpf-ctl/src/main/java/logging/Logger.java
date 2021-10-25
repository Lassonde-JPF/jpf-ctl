package logging;

import java.io.IOException;
import java.util.logging.FileHandler;

public class Logger {
	
	java.util.logging.Logger logger;
	
	String prefix;
	
	public Logger(String className) {
		logger = java.util.logging.Logger.getLogger(className);
		prefix = "";
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
	
	public Logger with(String prefix) {
		this.prefix = "[" + prefix + "]\t";
		return this;
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
