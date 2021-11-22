package label;

public class BooleanLocalVariable extends BinaryLabel {
	
	private static final String label_suffix = ".variable";
	
	private final String parameterTypes, variableName;
	
	public BooleanLocalVariable(String qualifiedName, String parameterTypes, String variableName) {
		super(BooleanLocalVariable.class.getSimpleName(), qualifiedName);
		this.parameterTypes = parameterTypes;
		this.variableName = variableName;
	}

	@Override
	public String labelDef() {
		return label_prefix + this.name + label_suffix;
	}

	@Override
	public String labelVal() {
		return this.qualifiedName + parameterTypes + ":" + variableName;
	}
	
}
