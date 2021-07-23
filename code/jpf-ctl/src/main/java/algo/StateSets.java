package algo;

import java.util.Set;

public class StateSets {
	public Set<Integer> sat;
	public Set<Integer> unsat;

	/**
	 * @param sat
	 * @param unsat
	 */
	public StateSets(Set<Integer> sat, Set<Integer> unsat) {
		this.sat = sat;
		this.unsat = unsat;
	}

	public Set<Integer> getSat() {
		return this.sat;
	}

	public Set<Integer> getUnSat() {
		return this.unsat;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof StateSets)) {
			return false;
		}

		StateSets ss = (StateSets) o;

		return this.sat.equals(ss.sat) && this.unsat.equals(ss.unsat);
	}

	@Override
	public String toString() {
		return "sat = " + sat + "\nunsat = " + unsat;
	}
}
