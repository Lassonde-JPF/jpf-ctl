package config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import algo.Main;

public class ConfigTest {

	@Test
	public void configTest() {
		String[] args = new String[] {
			"src/test/resources/config/myConfig.ctl"	
		};
		
		Main.main(args);
		
		System.out.println("uh");
		
		assertTrue(true);
	}
}
