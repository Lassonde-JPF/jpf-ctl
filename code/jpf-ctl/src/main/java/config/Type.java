package config;

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
	
	public static String labelDef(Type type) {
		String labelDef = "label." + type.toString();
		switch (type) {
		case BooleanStaticField:
		case IntegerStaticField:
			labelDef += ".field";
			break;
		case BooleanLocalVariable:
		case IntegerLocalVariable:
			labelDef += ".variable";
			break;
		case InvokedMethod:
		case ReturnedBooleanMethod:
		case ReturnedIntegerMethod:
		case ReturnedVoidMethod:
		case SynchronizedStaticMethod:
			labelDef += ".method";
			break;
		case ThrownException:
			labelDef += ".type";
		default:
			return null; // TODO this shouldn't happen
		}
		return labelDef;
	}
	
}
