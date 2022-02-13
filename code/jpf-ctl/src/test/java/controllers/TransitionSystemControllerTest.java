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

package controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.RepeatedTest;

import controller.CMD.TransitionSystemController;
import model.TransitionSystem;

/**
 * Tests the class that represents a transition system.
 * 
 * @author Franck van Breugel
 */
class TransitionSystemControllerTest {
	
	@RepeatedTest(100)
	void testToString() {
		// create random transition system
		TransitionSystem expected = new TransitionSystem();		
		String[] part = expected.toString().split("\n");
		int index = 0;
		try {
			PrintWriter writer = new PrintWriter("Example.tra");
			// transitions
			while (part[index].contains(TransitionSystem.TRANSITION_SEPARATOR)) {
				writer.println(part[index]);
				index++;
			}
			// partially explored states
			writer.println(part[index]);
			index++;
			// number of states
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
		
		TransitionSystem actual;
		try {
			actual = TransitionSystemController.parseTransitionSystem("Example", null, true);
			assertEquals(expected, actual);
		} catch (IOException e) {
			fail("Something went wrong with reading the file Example.tra or Example.lab");
		}
	}
}
