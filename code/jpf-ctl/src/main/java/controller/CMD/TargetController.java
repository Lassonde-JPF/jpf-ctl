package controller.CMD;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Properties;
import org.apache.commons.cli.ParseException;

import logging.Logger;
import model.Target;

public class TargetController {

	private static final String FILE_NAME = "target.properties";
	
	// For loading from target.properties
	public static Target parseTarget(String path) throws ParseException {
		Logger logger = new Logger(Target.class.getSimpleName());

		// Build actual filepath
		String filePath = (path == null) ? Paths.get(".").toAbsolutePath().normalize().toString() : path;
		filePath += File.separator + FILE_NAME;
		logger.info("Parsed path: " + filePath);

		try (InputStream input = new FileInputStream(filePath)) {
			Properties prop = new Properties();

			prop.load(input);

			// Load static arguments
			String name = (String) prop.remove("target");
			String classpath = (String) prop.remove("classpath");
			
			// Assume remaining arguments are jpf related
			Map<String, String> jpfArgs = prop.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> entry.getValue().toString()));

			// Check that target file exists and is correct
			String targetPath = classpath + File.separatorChar + name.replaceAll("\\.", "/") + ".class";
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
			// Log parsed target
			logger.info("Parsed Target: name=" + name + ", path=" + classpath + ", jpfargs=" + jpfArgs.toString());
			return new Target(name, classpath, jpfArgs);
		} catch (IOException e) {
			throw new ParseException("could not find properties file at " + filePath + e);
		}
	}
}
