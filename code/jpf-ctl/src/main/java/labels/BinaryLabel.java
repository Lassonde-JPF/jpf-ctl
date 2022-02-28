package labels;

import java.util.Random;

/**
 * BinaryLabel - A label with class AND value
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public abstract class BinaryLabel implements Label {

	// Attributes
	protected final String name, qualifiedName;

	/**
	 * Initializes this BinaryLabel with a given name (type) and fully qualified
	 * name
	 * 
	 * @param name          - Name of this label (BooleanLocalVariable,
	 *                      BooleanStaticField, etc.)
	 * @param qualifiedName
	 */
	public BinaryLabel(String name, String qualifiedName) {
		this.name = name;
		this.qualifiedName = qualifiedName;
	}

	/**
	 * public getter for qualifiedName
	 * 
	 * @return String - qualifiedName of this BinaryLabel
	 */
	public String getQualifiedName() {
		return this.qualifiedName;
	}

	/**
	 * Getter for this BinaryLabel's label definition
	 * 
	 * @return String - label definition of this BinaryLabel
	 */
	public abstract String labelDef();

	/**
	 * Getter for this BinaryLabel's value
	 * 
	 * @return String - label value of this BinaryLabel
	 */
	public abstract String labelVal();

	/**
	 * Getter for this BinaryLabel's Java Native Interface Representation
	 * 
	 * @return String - JNI representation of this BinaryLabel
	 */
	public abstract String getJNIName();

	/**
	 * Getter for this BinaryLabel's class definition
	 * 
	 * @return String - label definition of this BinaryLabel
	 */
	@Override
	public String classDef() {
		return BinaryLabel.label_prefix + this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;
		hashCode = prime * hashCode + this.name.hashCode();
		hashCode = prime * hashCode + this.qualifiedName.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			BinaryLabel other = (BinaryLabel) object;
			return this.name.equals(other.name) && this.qualifiedName.equals(other.qualifiedName);
		} else {
			return false;
		}
	}

	/**
	 * Randomly generates a BinaryLabel for the `labels.ReflectionExamples` class
	 * 
	 * @return BinaryLabel - a BinaryLabel for the `labels.ReflectionExamples` class
	 */
	static BinaryLabel random() {
		Random random = new Random();
		switch (random.nextInt(7)) {
		case 0:
			return new BooleanLocalVariable("labels.ReflectionExamples.main", "(java.lang.String[])", "variable",
					"src/test/java");
		case 1:
			return new BooleanStaticField("labels.ReflectionExamples.one", "src/test/java");
		case 2:
			return new InvokedMethod("labels.ReflectionExamples.setValue", "(boolean)", "src/test/java");
		case 3:
			return new ReturnedBooleanMethod("labels.ReflectionExamples.getRandom", "()", "src/test/java");
		case 4:
			return new ReturnedVoidMethod("labels.ReflectionExamples.setValue", "(boolean)", "src/test/java");
		case 5:
			return new SynchronizedStaticMethod("labels.ReflectionExamples.setValueSynchronized", "(boolean)",
					"src/test/java");
		case 6:
			return new ThrownException("java.util.zip.DataFormatException", "src/test/java");
		default:
			return null;
		}
	}
}
