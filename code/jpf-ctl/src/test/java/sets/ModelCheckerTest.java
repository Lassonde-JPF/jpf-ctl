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

package sets;

import static org.junit.jupiter.api.Assertions.*;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import formulas.ctl.CTLFormula;

/**
 * Tests the ModelChecker class.
 * 
 * @author Franck van Breugel
 */
public class ModelCheckerTest {
	/**
	 * Number of times each test is run.
	 */
	private static final int CASES = 1000;

	CTLFormula formula;

	sets.TransitionSystem setSystem;
	model.TransitionSystem bitSystem;

	sets.ModelChecker setChecker;
	model.ctl.CTLModelChecker bitChecker;

	@BeforeEach
	public void setUp() {
		formula = CTLFormula.random();

		setSystem = new sets.TransitionSystem(formula.getAtomicPropositions());
		// Build Partial for bit system
		BitSet partial = new BitSet(setSystem.getNumberOfStates());
		for (Integer state : setSystem.getPartial()) {
			partial.set(state);
		}
		// Build Successors for bit system
		Map<Integer, BitSet> successors = new HashMap<Integer, BitSet>();
		for (Entry<Integer, HashSet<Integer>> entry : setSystem.getSuccessors().entrySet()) {
			BitSet equivalent = new BitSet(setSystem.getNumberOfStates());
			for (Integer state : entry.getValue()) {
				equivalent.set(state);
			}
			successors.put(entry.getKey(), equivalent);
		}
		// Build Labelling for bit system
		Map<Integer, BitSet> labelling = new HashMap<Integer, BitSet>();
		for (Entry<Integer, HashSet<Integer>> entry : setSystem.getLabelling().entrySet()) {
			BitSet equivalent = new BitSet(setSystem.getNumberOfStates());
			for (Integer state : entry.getValue()) {
				equivalent.set(state);
			}
			labelling.put(entry.getKey(), equivalent);
		}
		bitSystem = new model.TransitionSystem(successors, setSystem.getIndices(), labelling, partial,
				setSystem.getNumberOfStates());
		assertEquals(setSystem.toString(), bitSystem.toString(), setSystem.toString() + "\n" + bitSystem.toString());

		setChecker = new sets.CTLModelChecker(setSystem);
		bitChecker = new model.ctl.CTLModelChecker(bitSystem);
	}

	/**
	 * Tests the formula true for a random system.
	 */
	@RepeatedTest(CASES)
	@DisplayName("Test Lower & Upper Bounds Coincide")
	public void testLowerAndUpperCoincide() {

		sets.Result setResult = setChecker.check(formula);
		model.Result bitResult = bitChecker.check(formula);

		// Translate BitSet Result to Set<Integer>
		HashSet<Integer> equivalentSetLower = getEquivalentSet(bitResult.getLower());
		HashSet<Integer> equivalentSetUpper = getEquivalentSet(bitResult.getUpper());
		assertAll(
				() -> assertEquals(setResult.getLower(), equivalentSetLower,
						"expected=HashSet<Integer>, actual=BitSet\n" + formula.toString() + "\n" + setSystem.toString()
								+ "\n" + "Bound: lower"),
				() -> assertEquals(setResult.getUpper(), equivalentSetUpper,
						"expected=HashSet<Integer>, actual=BitSet\n" + formula.toString() + "\n" + setSystem.toString()
								+ "\n" + "Bound: upper"));

		// Translate Set<Integer> Result to BitSet
		BitSet equivalentBitLower = getEquivalentBitSet(setResult.getLower());
		BitSet equivalentBitUpper = getEquivalentBitSet(setResult.getUpper());
		assertAll(
				() -> assertEquals(bitResult.getLower(), equivalentBitLower,
						"expected=BitSet, actual=HashSet<Integer>\n" + formula.toString() + "\n" + bitSystem.toString()
								+ "\n" + "Bound: lower"),
				() -> assertEquals(bitResult.getUpper(), equivalentBitUpper,
						"expected=BitSet, actual=HashSet<Integer>\n" + formula.toString() + "\n" + bitSystem.toString()
								+ "\n" + "Bound: upper"));

		// Test final "isValid" call
		assertEquals(setResult.isValid().toString(), bitResult.isValid().toString(),
				formula.toString() + "\n" + setSystem.toString());
	}

	// Convert BitSet -> HashSet<Integer>
	private HashSet<Integer> getEquivalentSet(BitSet other) {
		HashSet<Integer> equivalent = new HashSet<Integer>();
		for (int state = other.nextSetBit(0); state >= 0; state = other.nextSetBit(state + 1)) {
			equivalent.add(state);
		}
		return equivalent;
	}

	// Convert HashSet<Integer> -> BitSet
	private BitSet getEquivalentBitSet(HashSet<Integer> other) {
		BitSet equivalent = new BitSet();
		for (Integer state : other) {
			equivalent.set(state);
		}
		return equivalent;
	}
}
