/*
 * Copyright (C)  2021
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package core;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.junit.jupiter.api.Test;

/**
 * Tests whether JPF has been installed and can successfully run the HelloWorld example.
 * 
 * @author Franck van Breugel
 */
class JPFTest {

	@Test
	void test() {
		File home = new File(System.getProperty("user.home"));
		assertTrue(home.exists(), "home directory cannot be found");
		assertTrue(home.isDirectory(), "home is not a directory");
		
		File dotJpf = new File(home, ".jpf");
		assertTrue(dotJpf.exists(), ".jpf directory cannot be found");
		assertTrue(dotJpf.isDirectory(), ".jpf is not a directory");
		
		File siteProperties = new File(dotJpf, "site.properties");
		assertTrue(siteProperties.exists(), "site.properties file cannot be found");
		assertTrue(siteProperties.isFile(), "site.properties is not a file");
		
		try {
			Properties properties = new Properties();
			FileInputStream file = new FileInputStream(siteProperties);
			properties.load(file);
			file.close();
			assertTrue(properties.containsKey("jpf-core"), "site.properties file does not contain jpf-core");
			
			File jpfCore = new File(properties.getProperty("jpf-core"));
			assertTrue(jpfCore.exists(), "jpf-core directory cannot be found");
			assertTrue(jpfCore.isDirectory(), "jpf-core is not a directory");
			
			File build = new File(jpfCore, "build");
			assertTrue(build.exists(), "build directory cannot be found");
			assertTrue(build.isDirectory(), "build is not a directory");
			
			File jpfJar = new File(build, "jpf.jar");
			assertTrue(jpfJar.exists(), "jpf.jar file cannot be found");
			assertTrue(jpfJar.isFile(), "jpf.jar is not a file");
			
			URL[] urls = { jpfJar.toURI().toURL() };
			URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
			Class<?> clazz = Class.forName("gov.nasa.jpf.JPF", true, loader);
			
			Constructor<?> constructor = clazz.getConstructor(String[].class);
			String[] target = { "+target=HelloWorld" };
			Object[] args = { target };
			Object jpfInstance = constructor.newInstance(args);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream newStream = new PrintStream(stream);
			PrintStream oldStream = System.out;
			System.setOut(newStream);
			Method runMethod = clazz.getMethod("run");
			runMethod.invoke(jpfInstance);
			System.setOut(oldStream);
			String output = stream.toString();
			stream.close();
			
			assertTrue(output.startsWith("JavaPathfinder core system"), "JPF does not run correctly");
			assertTrue(output.contains("I won't say it!"), "JPF does not run correctly");
		} catch (IOException e) {
            fail("site.properties file cannot be read");
        } catch (ClassNotFoundException e) {
        	fail("gov.nasa.jpf.JPF cannot be found in jpf.jar");
        } catch (NoSuchMethodException e) {
        	fail("Constructor or run method of gov.nasa.jpf.JPF cannot be found");
        } catch (InvocationTargetException | IllegalAccessException  | InstantiationException | IllegalArgumentException e) {
        	fail("Constructor or run method of gov.nasa.jpf.JPF cannot be invoked");
        }
	}
}
