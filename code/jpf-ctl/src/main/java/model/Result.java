package model;

import java.util.BitSet;

/**
 * Result of model checking consists of two sets: a lower- and upperbound of the
 * satisfaction set of the formula. If a state is in the lowerbound then the
 * formula holds in that state. If a state is not in the upperbound then the
 * formula does not hold in that state. If a state is not in the lowerbound
 * but is in the upperbound then the partial transition system has insufficient
 * information to determine whether the CTL formula holds in that state.
 */
public class Result {
	
	/**
	 * Status - the status of this result
	 * @author mattw
	 *
	 */
	private static enum Status {
		VALID,
		INVALID,
		UNKNOWN;
	}
	
	// Attributes
	private BitSet lower;
	private BitSet upper;

	/**
	 * Initializes this result with the given lower- and upperbound.
	 * 
	 * @param lower the lowerbound of this result
	 * @param upper the upperbound of this result
	 */
	public Result(BitSet lower, BitSet upper) {
		this.lower = lower;
		this.upper = upper;
	}

	/**
	 * Returns the lowerbound of this result.
	 * 
	 * @return the lowerbound of this result
	 */
	public BitSet getLower() {
		return (BitSet) this.lower.clone();
	}

	/**
	 * Returns the upperbound of this result.
	 * 
	 * @return the upperbound of this result
	 */
	public BitSet getUpper() {
		return (BitSet) this.upper.clone();
	}

	
	/**
	 * Returns whether the corresponding pts for this result is partial
	 * 
	 * @return boolean - whether the corresponding pts is partial
	 */
	public boolean isPartial() {
		return !this.upper.equals(this.lower);
	}
	
	/**
	 * Returns whether this result is valid.
	 * 
	 * @return boolean - whether this result is valid
	 */
	public Status isValid() {
		if (this.lower.get(0)) {
			return Status.VALID;
		}
		if (!this.upper.get(0)) {
			return Status.INVALID;
		}
		return Status.UNKNOWN;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * this.lower.hashCode() + this.upper.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			Result other = (Result) object;
			return this.lower.equals(other.lower) && this.upper.equals(other.upper);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Result: " + this.isValid();
	}

}
