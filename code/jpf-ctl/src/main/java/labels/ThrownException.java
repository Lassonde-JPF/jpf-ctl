package labels;

import error.LabelReflectionException;

/**
 * ThrownException - A label representing a thrown exception
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class ThrownException extends BinaryLabel {

	// Attributes
	private static final String label_suffix = ".type";
	private final String JNIName;

	/**
	 * Initializes this ThrownException with a qualified name and path
	 * 
	 * @param qualifiedName - qualified name of this exception
	 * @param path          - classpath where this exception resides
	 */
	public ThrownException(String qualifiedName, String path) {
		super(ThrownException.class.getSimpleName(), qualifiedName);

		// Ensure class can be reflected
		if (Utils.extractClass(qualifiedName, path) == null) {
			throw new LabelReflectionException("there was a problem reflecting class " + qualifiedName);
		}

		// Set JNI Name representation
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
