package model;

import java.util.HashMap;
import java.util.Map;

public class Target {
	private String name, path;
	private Map<String, String> jpfArgs;
	
	// Defualt Constructor
	public Target(String name, String path, Map<String, String> jpfArgs) {
		this.name = name;
		this.path = path;
		this.jpfArgs = jpfArgs;
	}

	// For Static Generation
	public Target() {
		this("labels.ReflectionExamples", "bin/test", new HashMap<String, String>());
	}
	
	@Override
	public String toString() {
		return "Target: name=" + this.name + ", path=" + this.path + ", jpfArgs=" + this.jpfArgs;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}
	
	public Map<String, String> getJpfArgs() {
		return this.jpfArgs;
	}

}
