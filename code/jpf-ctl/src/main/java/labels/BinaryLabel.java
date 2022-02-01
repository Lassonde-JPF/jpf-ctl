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
		switch (random.nextInt(6)) {
		case 0:
			// 'return new BooleanLocalVariable();
		case 1:
			// return new BooleanStaticField();
		case 2:
			// return new InvokedMethod();
		case 3:
			// return new ReturnedBooleanMethod();
		case 4:
			// return new ReturnedVoidMethod();
		case 5: 
			// return new SynchronizedStaticMethod();
		case 6:
			// return new ThrownException();
		default:
			return null;
		}
	}
}
