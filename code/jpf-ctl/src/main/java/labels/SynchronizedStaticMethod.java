package labels;

public class SynchronizedStaticMethod extends BinaryLabel {

	private static final String label_suffix = ".method";
	private final String parameterTypes;
	
	public SynchronizedStaticMethod(String qualifiedName, String parameterTypes) {
		super(SynchronizedStaticMethod.class.getSimpleName(), qualifiedName);
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
