package label;

public class ThrownException extends BinaryLabel {

	private static final String label_suffix = ".type";
	
	public ThrownException(String qualifiedName) {
		super(ThrownException.class.getSimpleName(), qualifiedName);
	}

	@Override
	public String labelDef() {
		return label_prefix + this.name + label_suffix;
	}

	@Override
	public String labelVal() {
		return this.qualifiedName;
	}

}
