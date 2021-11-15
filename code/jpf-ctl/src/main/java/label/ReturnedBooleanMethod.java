package label;

public class ReturnedBooleanMethod extends BinaryLabel {

	private static final String label_suffix = ".method";
	private final String parameterTypes;
	
	public ReturnedBooleanMethod(String qualifiedName, String parameterTypes) {
		super(ReturnedBooleanMethod.class.getSimpleName(), qualifiedName);
		this.parameterTypes = parameterTypes;
	}

	@Override
	public String labelDef() {
		return label_prefix + this.name + label_suffix;
	}

	@Override
	public String labelVal() {
		return this.qualifiedName + parameterTypes;
	}

	
}
