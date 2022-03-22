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

	
	/**
	 * Tests the formula true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testDifferenceSetToBit() {
		
		CTLFormula formula = CTLFormula.random();
		
		sets.TransitionSystem setSystem = new sets.TransitionSystem(formula.getAtomicPropositions());
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
		model.TransitionSystem bitSystem = new model.TransitionSystem(successors, setSystem.getIndices(), labelling, partial, setSystem.getNumberOfStates());
		// Ensure resulting systems are the same
		assertEquals(setSystem.toString(), bitSystem.toString(), setSystem.toString() + "\n" + bitSystem.toString());
		
		sets.ModelChecker setChecker = new sets.CTLModelChecker(setSystem);
		model.ctl.CTLModelChecker bitChecker = new model.ctl.CTLModelChecker(bitSystem);
		sets.Result setResult = setChecker.check(formula);
		model.Result bitResult = bitChecker.check(formula);
		
		// Translate BitSet Result to Set<Integer>
		HashSet<Integer> equivalentSetLower = new HashSet<Integer>();
		HashSet<Integer> equivalentSetUpper = new HashSet<Integer>();
		BitSet bitLower = bitResult.getLower();
		BitSet bitUpper = bitResult.getUpper();
		for (int state = bitLower.nextSetBit(0); state >= 0; state = bitLower.nextSetBit(state+1)) {
			equivalentSetLower.add(state);
		}
		for (int state = bitUpper.nextSetBit(0); state >= 0; state = bitUpper.nextSetBit(state+1)) {
			equivalentSetUpper.add(state);
		}
		
		assertEquals(setResult.getLower(), equivalentSetLower, formula.toString() + "\n" + setSystem.toString());
		assertEquals(setResult.getUpper(), equivalentSetUpper, formula.toString() + "\n" + setSystem.toString());
		
		// Translate Set<Integer> Result to BitSet
		BitSet equivalentBitLower = new BitSet(bitSystem.getNumberOfStates());
		BitSet equivalentBitUpper = new BitSet(bitSystem.getNumberOfStates());
		for (Integer state : setResult.getLower()) {
			equivalentBitLower.set(state);
		}
		for (Integer state : setResult.getUpper()) {
			equivalentBitUpper.set(state);
		}
		
		assertEquals(bitResult.getLower(), equivalentBitLower, formula.toString() + "\n" + bitSystem.toString());
		assertEquals(bitResult.getUpper(), equivalentBitUpper, formula.toString() + "\n" + bitSystem.toString());
		
		assertEquals(setResult.isValid().toString(), bitResult.isValid().toString(), formula.toString() + "\n" + setSystem.toString());		
	}
}
