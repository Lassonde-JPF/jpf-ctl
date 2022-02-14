package labels;

import java.util.Random;

public abstract class BinaryLabel implements Label {
	
	protected final String name, qualifiedName;
	
	public BinaryLabel(String name, String qualifiedName) {
		this.name = name;
		this.qualifiedName = qualifiedName;
	}
	
	public String getQualifiedName() {
		return this.qualifiedName;
	}
	
	public abstract String labelDef();
	
	public abstract String labelVal();
	
	public abstract String getJNIName();
	
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
	
	static BinaryLabel random() {
		Random random = new Random();
		switch (random.nextInt(7)) {
		case 0:
			return new BooleanLocalVariable("labels.ReflectionExamples.main", "(java.lang.String[])", "variable", "src/test/java");
		case 1:
			return new BooleanStaticField("labels.ReflectionExamples.one", "src/test/java");
		case 2:
			return new InvokedMethod("labels.ReflectionExamples.setValue", "(boolean)", "src/test/java");
		case 3:
			return new ReturnedBooleanMethod("labels.ReflectionExamples.getRandom", "()", "src/test/java");
		case 4:
			return new ReturnedVoidMethod("labels.ReflectionExamples.setValue", "(boolean)", "src/test/java");
		case 5: 
			return new SynchronizedStaticMethod("labels.ReflectionExamples.setValueSynchronized", "(boolean)", "src/test/java");
		case 6:
			return new ThrownException("java.util.zip.DataFormatException", "src/test/java");
		default:
			return null;
		}
	}
}
