package model;

import java.util.HashMap;
import java.util.Map;

public class Target {
	private String name, path;
	private Map<String, String> jpfArgs;
	private LogicType logic;
	
	// Defualt Constructor
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
		return "Target: name=" + this.name + ", path=" + this.path + ", logic.language=" + this.logic + ", jpfArgs=" + this.jpfArgs;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}
	
	public LogicType getLogic() {
		return this.logic;
	}
	
	public Map<String, String> getJpfArgs() {
		return this.jpfArgs;
	}

}
