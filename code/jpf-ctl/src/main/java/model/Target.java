package model;

import java.util.Map;

public class Target {
	private String name, path, enumerateRandom;
	private Map<String, String> jpfArgs;

	// For Static Generation
	public Target() {
		this.name = "labels.ReflectionExamples";
		this.path = "bin/test";
	}
	
	// Defualt Constructor
	public Target(String name, String path, Map<String, String> jpfArgs) {
		this.name = name;
		this.path = path;
		this.jpfArgs = jpfArgs;
	}

	@Override
	public String toString() {
		return "Target: name=" + this.name + ", path=" + this.path + ", jpfArgs=" + this.jpfArgs + ", enumerateRandom=" + this.enumerateRandom;
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
