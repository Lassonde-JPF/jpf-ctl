package label;

public abstract class UnaryLabel implements Label {
	
	String name;
	
	public UnaryLabel(String name) {
		this.name = name;
	}
	
	@Override
	public String classDef() {
		return UnaryLabel.label_prefix + this.name;
	}
}
