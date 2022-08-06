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

package jpf.logic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.RepeatedTest;

/**
 * Tests the class that represents a partial transition system.
 * 
 * @author Franck van Breugel
 */
class PartialTransitionSystemTest {

	/**
	 * The number of times a test involving randomness is repeated.
	 */
	private static final int TIMES = 1000;
	
	@RepeatedTest(TIMES)
	void testToString() {
		// create random transition system
		PartialTransitionSystem expected = new PartialTransitionSystem();		
		String[] part = expected.toString().split("\n");
		int index = 0;
		try {
			PrintWriter writer = new PrintWriter("Example.tra");
			// number of states and transitions
			writer.println(part[index]);
			index++;
			// transitions
			while (part[index].contains(PartialTransitionSystem.TRANSITION_SEPARATOR)) {
				writer.println(part[index]);
				index++;
			}
			// partially explored states
			writer.println(part[index]);
			index++;
			writer.close();
		} catch (FileNotFoundException e) {
			fail("Something went wrong with writing to the file Example.tra");
		}
		
		try {
			PrintWriter writer = new PrintWriter("Example.lab");
			while (index < part.length) {
				writer.println(part[index]);
				index++;
			}
			writer.close();
		} catch (FileNotFoundException e) {
			fail("Something went wrong with writing to the file Example.lab");
		}
		
		PartialTransitionSystem actual;
		try {
			actual = new PartialTransitionSystem("Example");
			assertEquals(expected, actual);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Something went wrong with reading the file Example.tra or Example.lab");
		}
	}
}
