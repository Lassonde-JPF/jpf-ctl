package labels;

public class End extends UnaryLabel{

	public End() {
		super(End.class.getSimpleName());
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
