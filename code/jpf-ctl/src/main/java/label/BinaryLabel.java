package label;

public abstract class BinaryLabel implements Label {
	
	protected final String name;
	protected final String qualifiedName;
	
	public BinaryLabel(String name, String qualifiedName) {
		this.name = name;
		this.qualifiedName = qualifiedName;
	}
	
	@Override
	public String classDef() {
		return BinaryLabel.label_prefix + this.name;
	} 
	
	public abstract String labelDef();
	
	public abstract String labelVal();
}
