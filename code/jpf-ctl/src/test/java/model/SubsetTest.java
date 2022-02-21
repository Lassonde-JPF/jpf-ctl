/*
 * Copyright (C)  2022
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

package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.BitSet;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;

/**
 * Tests the subset method of the ModelChecker class.
 * 
 * @author Franck van Breugel
 */
class SubsetTest {
	
	/**
	 * Randomness.
	 */
	private static final Random random = new Random();

	/**
	 * Number of times that each test is repeated.
	 */
	private static final int TIMES = 1000;
	
	/**
	 * Elements of set range from (inclusive) 0 to RANGE (exclusive).
	 */
	private static final int RANGE = 100;
	
	/**
	 * Probability that an element occurs in a random set.
	 */
	private static final double PROBABILITY = 0.1;
	
	/**
	 * Returns a random set.
	 * 
	 * @return a random set.
	 */
	private static BitSet randomSet() {
		BitSet set = new BitSet();
		for (int i = 0; i < RANGE; i++) {
			if (random.nextDouble() < PROBABILITY) {
				set.set(i);
			}
		}
		return set;
	}
	
	/**
	 * Tests the subset method for random sets.
	 */
	@RepeatedTest(TIMES)
	void test() {
		final int ADD = 10;
		
		BitSet smaller = randomSet();
		BitSet bigger = (BitSet) smaller.clone();
		int number = 1 + random.nextInt(ADD);
		for (int n = 0; n < number; n++) {
			bigger.set(random.nextInt(RANGE));
		}
		assertTrue(ModelChecker.subset(smaller, bigger), "Failed for smaller = " + smaller + " and bigger = " + bigger);
		if (bigger.equals(smaller)) {
			assertTrue(ModelChecker.subset(bigger, smaller), "Failed for smaller = " + smaller + " and bigger = " + bigger);
		} else {
			assertFalse(ModelChecker.subset(bigger, smaller), "Failed for smaller = " + smaller + " and bigger = " + bigger);
		}
	}
}
