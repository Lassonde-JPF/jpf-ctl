package labels;

public class BooleanStaticField extends BinaryLabel {

	private static final String label_suffix = ".field"; 
	
	public BooleanStaticField(String qualifiedName) {
		super(BooleanStaticField.class.getSimpleName(), qualifiedName);
	}

	@Override
	public String labelDef() {
		return label_prefix + this.name + BooleanStaticField.label_suffix;
	}

	@Override
	public String labelVal() {
		return this.qualifiedName;
	}
	
	@Override
	public String toString() {
		return this.qualifiedName.replace('.', '_');
	}

}
