package label;

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
		switch (type) {
		case BooleanLocalVariable:
			break;
		case BooleanStaticField:
			break;
		case End:
		case Initial:
			return true;
		case IntegerLocalVariable:
			break;
		case IntegerStaticField:
			break;
		case InvokedMethod:
			break;
		case ReturnedBooleanMethod:
			break;
		case ReturnedIntegerMethod:
			break;
		case ReturnedVoidMethod:
			break;
		case SynchronizedStaticMethod:
			break;
		case ThrownException:
			break;
		default:
			return false;
		}
		return false;
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
