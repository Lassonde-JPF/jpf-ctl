package labels;

import java.util.Random;

public abstract class UnaryLabel implements Label {
	
	private String name;
	
	public UnaryLabel(String name) {
		this.name = name;
	}
	
	@Override
	public String classDef() {
		return UnaryLabel.label_prefix + this.name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name + this.classDef();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			UnaryLabel other = (UnaryLabel) object;
			return this.name.equals(other.name) && this.classDef().equals(other.classDef());
		} else {
			return false;
		}
	}
	
	static UnaryLabel random() {
		return new Random().nextBoolean() ? new End() : new Initial();
	}
}

