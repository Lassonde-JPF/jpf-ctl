package label;

// TODO refactor via ctl package 
// Inherit type

public enum Type {
	Initial,
	End,
	BooleanStaticField,
	IntegerStaticField,
	BooleanLocalVariable,
	IntegerLocalVariable,
	InvokedMethod,
	ReturnedBooleanMethod,
	ReturnedIntegerMethod,
	ReturnedVoidMethod,
	SynchronizedStaticMethod,
	ThrownException;

	public static boolean validate(Type type, String qualifiedName) {
		return true;
	}
}



