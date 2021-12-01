package config;

public class Target {
	private String name, path;

	public Target(String className, String path) {
		this.name = className;
		this.path = path;
	}

	@Override
	public String toString() {
		return "Target Name: " + this.name + "\nTarget Path: " + this.path;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

}
