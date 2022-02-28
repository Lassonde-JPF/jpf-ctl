package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Target - the target associated with a model checking run. Often used for
 * running JPF and configuring various controllers.
 * 
 * @author mattw
 *
 */
public class Target {

	// Attributes
	private String name, path;
	private Map<String, String> jpfArgs;
	private LogicType logic;

	/**
	 * Initializes this Target with a name, path, logic type, and jpf arguments
	 * 
	 * @param name    - name of this target (Name of Class)
	 * @param path    - classpath where this target resides
	 * @param logic   - type of logic to associate with this model checking run
	 * @param jpfArgs - arguments to pass to jpf when running
	 */
	public Target(String name, String path, LogicType logic, Map<String, String> jpfArgs) {
		this.name = name;
		this.path = path;
		this.logic = logic;
		this.jpfArgs = jpfArgs;
	}

	// For Static Generation
	public Target() {
		this("labels.ReflectionExamples", "bin/test", LogicType.CTL, new HashMap<String, String>());
	}

	@Override
	public String toString() {
		return "Target: name=" + this.name + ", path=" + this.path + ", logic.language=" + this.logic + ", jpfArgs="
				+ this.jpfArgs;
	}

	/**
	 * Default getter for target name
	 * 
	 * @return target name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Default getter for target path
	 * 
	 * @return target path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Default getter for target logic
	 * 
	 * @return target logic
	 */
	public LogicType getLogic() {
		return this.logic;
	}

	/**
	 * Default getter for target jpf args
	 * 
	 * @return target jpf args
	 */
	public Map<String, String> getJpfArgs() {
		return this.jpfArgs;
	}

}
