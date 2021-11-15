package label;

public class ReturnedVoidMethod extends BinaryLabel {

	private static final String label_suffix = ".method";
	private final String parameterTypes;
	
	public ReturnedVoidMethod(String qualifiedName, String parameterTypes) {
		super(ReturnedVoidMethod.class.getSimpleName(), qualifiedName);
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
