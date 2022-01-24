package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.cli.ParseException;

import logging.Logger;

public class Target {
	private String name, path, args, enumerateRandom;
	private static final String FILE_NAME = "target.properties";

	private Logger logger;

	// For loading from target.properties
	public Target(String path) throws ParseException {
		this.logger = new Logger(Target.class.getSimpleName());

		// Build actual filepath
		String filePath = (path == null) ? Paths.get(".").toAbsolutePath().normalize().toString() : path;
		filePath += File.separator + FILE_NAME;
		this.logger.info("Parsed path: " + filePath);

		try (InputStream input = new FileInputStream(filePath)) {
			Properties prop = new Properties();

			prop.load(input);

			this.name = prop.getProperty("target");
			this.path = prop.getProperty("classpath");
			this.args = prop.getProperty("args");

			this.enumerateRandom = prop.getProperty("enumerateRandom");

			// Check that target file exists and is correct
			String targetPath = this.path + File.separatorChar + this.name.replaceAll("\\.", "/") + ".class";
			try {
				String extension = targetPath.substring(targetPath.lastIndexOf("."));
				if (!extension.equals(".class")) {
					throw new ParseException("file has the wrong extension, expected '" + ".class" + "' for "
							+ targetPath + " but was " + extension);
				}
			} catch (IndexOutOfBoundsException e) {
				throw new ParseException("file does not contain an extension, expected '" + ".class" + "' for " + path);
			}
			File targetFile = new File(targetPath);
			if (!targetFile.exists()) {
				throw new ParseException("could not find file specified by: " + targetPath);
			}
		} catch (IOException e) {
			throw new ParseException("could not find properties file at " + filePath + e);
		}
		
		// Log parsed target
		this.logger.info("Parsed Target: name=" + this.name + ", path=" + this.path + ", args=" + this.args);
	}

	@Override
	public String toString() {
		return "Target: name=" + this.name + ", path=" + this.path + "args=" + this.args;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

	public String getArgs() {
		return this.args;
	}

	public String getEnumerateRandom() {
		return this.enumerateRandom;
	}

}
