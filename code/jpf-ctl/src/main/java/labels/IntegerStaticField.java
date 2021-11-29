package labels;

public class IntegerStaticField extends BinaryLabel {

	private static final String label_suffix = ".field"; 
	
	public IntegerStaticField(String qualifiedName) {
		super(IntegerStaticField.class.getSimpleName(), qualifiedName);
	}

	@Override
	public String labelDef() {
		return IntegerStaticField.label_prefix + this.name + IntegerStaticField.label_suffix;
	}

	@Override
	public String labelVal() {
		return this.qualifiedName;
	}

}
