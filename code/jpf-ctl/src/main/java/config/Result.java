package config;

import ctl.Formula;

public class Result {

	private boolean valid;
	private Formula formula;
	private String counterExample;
	private Target target;

	public Result(Target target, Formula formula, String counterExample, boolean valid) {
		this.valid = valid;
		this.formula = formula;
		this.counterExample = counterExample;
		this.target = target;
	}

	@Override
	public String toString() {
		return "\nModel Checking Finished\nFor the selected class:\t" + this.target.getName()
				+ "\nAnd the written formula:\t" + this.formula + "\nIt has been determined that the formula "
				+ (this.valid ? "holds in the initial state and is considered valid for this system."
						: "does not hold in the initial state and is considered invalid for this system."
								+ "\nA counter example can be seen below:\n" + this.counterExample);
	}

	public boolean isValid() {
		return this.valid;
	}
}
