package labels;

import error.LabelReflectionException;

public class ThrownException extends BinaryLabel {

	private static final String label_suffix = ".type";
	private final String JNIName;
	
	public ThrownException(String qualifiedName, String path) {
		super(ThrownException.class.getSimpleName(), qualifiedName);
		
		if (Utils.extractClass(qualifiedName, path) == null) {
			throw new LabelReflectionException("there was a problem reflecting class " + qualifiedName);
		}
		
		this.JNIName = qualifiedName.replace('.', '_');
	}

	@Override
	public String getJNIName() {
		return this.JNIName;
	}
	
	@Override
	public String labelDef() {
		return label_prefix + this.name + label_suffix;
	}

	@Override
	public String labelVal() {
		return this.qualifiedName;
	}
	
	@Override
	public String toString() {
		return this.name + " " + this.labelVal();
	}

}
