package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.cli.ParseException;

public class Target {
	private String name, path, args;
	
	private static final String FILE_NAME = "target.properties"; 
	
	// For loading from target.properties file :shrugs:
	public Target(String path) throws ParseException {
		String filePath;
		if (path == null) {
			filePath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separatorChar + FILE_NAME;
		} else {
			filePath = path + File.separatorChar + FILE_NAME;
		}
		try (InputStream input = new FileInputStream(filePath)) {
			Properties prop = new Properties();
			
			prop.load(input);
			
			this.name = prop.getProperty("target");
			this.path = prop.getProperty("classpath");
			this.args = prop.getProperty("args");
			
			// Check that target file exists and is correct
			String targetPath = this.path + File.separatorChar + this.name.replaceAll("\\.", "/") + ".class";
			try {
				String extension = targetPath.substring(targetPath.lastIndexOf("."));
				if (!extension.equals(".class")) {
					throw new ParseException("file has the wrong extension, expected '" + ".class" + "' for "
							+ targetPath + " but was " + extension);
				}
			} catch (IndexOutOfBoundsException e) {
				throw new ParseException(
						"file does not contain an extension, expected '" + ".class" + "' for " + path);
			}
			File targetFile = new File(targetPath);
			if (!targetFile.exists()) {
				throw new ParseException("could not find file specified by: " + targetPath);
			}
			
		} catch (Exception e) {
			throw new ParseException("could not find properties file at " + filePath + e);
		}
	}

	@Override
	public String toString() {
		return "Target Name: " + this.name + "\nTarget Path: " + this.path + "\nTarget Args: " + this.args;
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

}
