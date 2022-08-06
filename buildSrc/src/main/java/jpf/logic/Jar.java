/*
 * Copyright (C) 2021  Franck van Breugel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You can find a copy of the GNU General Public License at
 * <http://www.gnu.org/licenses/>.
 */

package jpf.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.gradle.api.GradleException;

/**
 * Utility class to find the full path of the jar file of jpf-core.
 * 
 * @author Franck van Breugel
 */
public class Jar {

	/**
	 * Properties of JPF's site.properties file.
	 */
	private static Properties configuration = getConfiguration();

	private static Properties getConfiguration() {
		// find home directory
		File home = new File(System.getProperty("user.home"));
		if (!home.exists()) {
			throw new GradleException("home directory cannot be found");
		}
		if (!home.isDirectory()) {
			throw new GradleException("home is not a directory");
		}

		// find .jpf directory	
		File dotJpf = new File(home, ".jpf");
		if (!dotJpf.exists()) {
			throw new GradleException(".jpf directory cannot be found");
		}
		if (!dotJpf.isDirectory()) {
			throw new GradleException(".jpf is not a directory");
		}

		// find site.properties file	
		File siteProperties = new File(dotJpf, "site.properties");
		if (!siteProperties.exists()) {
			throw new GradleException("site.properties file cannot be found");
		}
		if (!siteProperties.isFile()) {
			throw new GradleException("site.properties is not a file");
		}

		try {
			// load site.properties file
			Properties configuration = new Properties();
			InputStream stream = new FileInputStream(siteProperties);
			configuration.load(stream);
			// add system properties
			configuration.putAll(System.getenv());
			return configuration;
		} catch (FileNotFoundException e) {
			throw new GradleException("site.properties file cannot be found");
		} catch (IOException e) {
			throw new GradleException("site.properties file cannot be read");
		}
	}

	/**
	 * Returns the full path of the jar file of jpf-core.
	 * 
	 * @return the full path of the jar file of jpf-core
	 * @throws GradleException if something goes wrong
	 */
	public static String getJPF() {
		// find jpf-core directory
		if (!configuration.containsKey("jpf-core")) {
			throw new GradleException("site.properties file does not contain jpf-core");
		}
		String path = configuration.getProperty("jpf-core");
		File jpfCore = new File(path);			
		if (!jpfCore.exists()) {
			throw new GradleException("jpf-core directory cannot be found");
		}
		if (!jpfCore.isDirectory()) {
			throw new GradleException("jpf-core is not a directory");
		}

		// find build directory
		File build = new File(jpfCore, "build");
		if (!build.exists()) {
			throw new GradleException("build directory cannot be found");
		}
		if (!build.isDirectory()) {
			throw new GradleException("build is not a directory");
		}

		// find jpf.jar		
		File jpfJar = new File(build, "jpf.jar");
		if (!jpfJar.exists()) {
			throw new GradleException("jpf.jar file cannot be found");
		}
		if (!jpfJar.isFile()) {
			throw new GradleException("jpf.jar is not a file");
		}

		return jpfJar.toString();
	}
}
