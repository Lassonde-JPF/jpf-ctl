package model;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import formulas.Formula;

public abstract class ModelChecker {

	// partial transition system
	protected final TransitionSystem system;

	// cache of lower- and upperbounds of the satisfaction set for formulas
	protected Map<Formula, Result> cache;

	public ModelChecker(TransitionSystem system) {
		this.system = system;
		this.cache = new HashMap<Formula, Result>();
	}

	public abstract Result check(Formula formula);

	/**
	 * Tests whether the smaller set is a subset of the larger set.
	 * 
	 * @param smaller a set
	 * @param bigger  a set
	 * @return true if the smaller set is a subset of the larger set, false
	 *         otherwise
	 */
	protected static boolean subset(BitSet smaller, BitSet bigger) {
		BitSet copy = (BitSet) bigger.clone();
		copy.and(smaller);
		return smaller.equals(copy);
	}
}
