package labels;

public abstract class UnaryLabel implements Label {
	
	protected String name;
	
	public UnaryLabel(String name) {
		this.name = name;
	}
	
	@Override
	public String classDef() {
		return UnaryLabel.label_prefix + this.name;
	}
	
	public String getName() {
		return this.name;
	}
}

