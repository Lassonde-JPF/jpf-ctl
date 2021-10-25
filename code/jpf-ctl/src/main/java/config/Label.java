package config;

public class Label {
	private Type type;
	private String qualifiedName;
	
	public Label(Type type, String qualifiedName) {
		this.type = type;
		this.qualifiedName = qualifiedName;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public String getQualifiedName() {
		return this.qualifiedName;
	}
	
	@Override
	public String toString() {
		return this.type + " " + this.qualifiedName;
	}
	
}
