package logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;

public class Logger {
	
	private static final String LOG_EXTENSION = ".log.dtd";
	
	private String prefix;
	private boolean enabled;
	private java.util.logging.Logger logger;
	
	public Logger(String className, String prefix) {
		this.logger = java.util.logging.Logger.getLogger(className);
		this.prefix = "[" + prefix + "] ";
		this.enabled = true;
	}

	public void info(String msg) {
		if (this.enabled) {
			logger.info(this.prefix + msg);
		}
	}
	
	public void warning(String msg) {
		if (this.enabled) {
			logger.warning(this.prefix + msg);
		}
	}
	
	public void severe(String msg) {
		if (this.enabled) {
			logger.severe(this.prefix + msg);
		}
	}
	
	public void fine(String msg) {
		if (this.enabled) {
			logger.fine(this.prefix + msg);
		}
	}
	
	public boolean setEnabled(boolean val) {
		this.enabled = val;
		return this.enabled;
	}
	
	public void setOutputFile(String fileName) throws SecurityException, IOException {
		File logFile = new File("logs/" + fileName + LOG_EXTENSION);
		logFile.getParentFile().mkdirs();
		logger.addHandler(new FileHandler(logFile.getCanonicalPath(), 8096, 1, true));
		logger.setUseParentHandlers(true);
	}

}
