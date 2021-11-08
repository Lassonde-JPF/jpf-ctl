package config;

public class Target {
	private String name, path, packageName;

	private boolean hasPackage;

	public Target(String className, String path) {
		this.name = className;
		this.path = path;
		this.hasPackage = false;
	}

	public Target(String className, String packageName, String path) {
		this.name = className;
		this.path = path;
		this.packageName = packageName;
		this.hasPackage = true;
	}

	@Override
	public String toString() {
		return "Target Name: " + this.name + "\nTarget Path: " + this.path
				+ (this.hasPackage ? "\nPackage Name: " + this.packageName : "");
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

	public String getPackage() {
		return this.packageName;
	}

	public boolean hasPackage() {
		return this.hasPackage;
	}

}
