package labels;

public class Initial extends UnaryLabel {

	public Initial() {
		super(Initial.class.getSimpleName());
	}
	
	@Override
	public String toString() {
		return this.getName();
	}

}
